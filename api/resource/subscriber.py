from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_provider, lp_user, lp_subscriber
import uuid
from util import calculate_min_dist
from polyline import GPolyCoder as gpc
import collections
import time

# !the final call on abstracting this and including it into a configuration file has to be made, so the code looks cleaner!

# Request parsers

parser = reqparse.RequestParser()
#enable when key format is done :
#parser.add_argument( 'key', dest='app_id', type=inputs.regex('^.{10}$'), required=True, help='Application id' )

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()

## get
get_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_parser.add_argument( 'id', dest='lp_uid', type=int, required=True, help='The user\'s id' )

## post
post_parser.add_argument( 'key', dest='app_id', type=str, required=True )
#post_parser.add_argument( 'eta', dest='trip_creation_time', type=str, required=True )
post_parser.add_argument( 'route', dest='encroute', type=str, required=True )

# Response fields

get_field = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'gender': fields.String(attribute='gender'),
    'email': fields.String(attribute='email'),
    'image_uri': fields.String(attribute='image_url'),
    'designation': fields.String(attribute='org_title'),
    'department': fields.String(attribute='org_dept'),
    'about': fields.String(attribute='about_me'),
    'route': fields.String(attribute='encroute'),
    'eta': fields.String(attribute='trip_creation_time'),
}

post_geo = {
    'lat': fields.String(attribute='lat'),
    'lon': fields.String(attribute='lng'),
}

post_subfield = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
    'image': fields.String(attribute='image_url'),
    'distance': fields.String(attribute='distance'),
    'start': fields.Nested(post_geo),
    'stop': fields.Nested(post_geo),
}

data_field = {
    'providers': fields.List(fields.Nested(post_subfield))   
}

post_field = {
    'status' : fields.String(attribute='status'),
    'data' : data_field,
    'message' : fields.String(attribute='message'),
}

# Resource class

class Subscriber(Resource):
    @marshal_with(get_field)
    def get(self):
        args = get_parser.parse_args()
        # if app_id is valid
        user = None
        subscriber = None
        if lp_user.query.filter_by(app_id=args.app_id).first() != None:
            if lp_subscriber.query.filter_by(lp_uid=args.lp_uid).first() != None:
                user = lp_user.query.get(args.lp_uid)
                subscriber = lp_subscriber.query.get(args.lp_uid)
                user.eta=subscriber.trip_creation_time
                user.route=subscriber.encroute
                print user.eta,user.route
            else:
                return user
        return user

    @marshal_with(post_field)
    def post(self):
        args = post_parser.parse_args()
        routeid = str(uuid.uuid4())
        trip_creation_time = time.time()
        lp_uid = lp_user.query.with_entities(lp_user.lp_uid).filter_by(app_id=args.app_id).first()
        db.session.add(lp_subscriber(lp_uid[0], trip_creation_time, routeid, args.encroute))
        # find the distance
        listpros = collections.defaultdict(list)
        for pros, user in db.session.query(lp_provider, lp_user).join(lp_user, lp_provider.lp_uid == lp_user.lp_uid).all():
            prosd = pros._asdict()
            userd = user._asdict()
            # there has to be a better way to do this!
            temp = collections.defaultdict(list)
            points = gpc().decode(args.encroute)
            point = points[-1]
            temp['lp_uid'] = userd['lp_uid']
            temp['display_name'] = userd['display_name']
            temp['trip_creation_time'] = prosd['trip_creation_time']
            temp['image_url'] = userd['image_url']
            temp['distance'] = calculate_min_dist(prosd['encroute'], point)
            pointc = collections.defaultdict(dict)
            (pointc['lat'], pointc['lng']) = points[0]
            temp['start'] = pointc
            (pointc['lat'], pointc['lng']) = points[-1]
            temp['stop'] = pointc
            listpros['providers'].append(temp)
            listpros['status'] = 'OK'
            listpros['message'] = 'some message'
        db.session.commit()
        print listpros
        return listpros
