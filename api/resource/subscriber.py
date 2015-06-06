from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_provider, lp_user, lp_subscriber, lp_match
import uuid
from util import calculate_min_dist
from polyline import GPolyCoder as gpc
import collections
import time
import json

# !the final call on abstracting this and including it into a configuration file has to be made, so the code looks cleaner!

# Request parsers

parser = reqparse.RequestParser()
#enable when key format is done :
#parser.add_argument( 'key', dest='app_id', type=inputs.regex('^.{10}$'), required=True, help='Application id' )

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()
get_request_status = parser.copy()
## get
get_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_parser.add_argument( 'id', dest='lp_uid', type=int, required=True, help='The user\'s id' )

## get
get_request_status.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_request_status.add_argument( 'provider', dest='pid', type=int, required=True, help='The user\'s id' )

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

# SubscriberRefresh GET response 

get_refresh_geo = {
    'lat': fields.String(attribute='lat'),
    'lon': fields.String(attribute='lng'),
}

get_refresh_subfield = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'phone' : fields.String(attribute='phone'),
    'org_title' : fields.String(attribute='org_title'),
    'org_name' : fields.String(attribute='org_name'),
    'trip_elapsed_time' : fields.String(attribute='trip_elapsed_time'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
    'image': fields.String(attribute='image_url'),
    'distance': fields.String(attribute='distance'),
    'provider_route' : fields.String(attribute='route'),
    'start': fields.Nested(get_refresh_geo),
    'stop': fields.Nested(get_refresh_geo),
}

get_refresh_data = {
    'providers': fields.List(fields.Nested(get_refresh_subfield))
}

get_refresh_field = {
    'status' : fields.String(attribute='status'),
    'data' : get_refresh_data,
    'message' : fields.String(attribute='message'),
}


# Subscriber POST response 


post_geo = {
    'lat': fields.String(attribute='lat'),
    'lon': fields.String(attribute='lng'),
}

post_subfield = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'org_title' : fields.String(attribute='org_title'),
    'org_name' : fields.String(attribute='org_name'),
    'trip_elapsed_time' : fields.String(attribute='trip_elapsed_time'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
    'image': fields.String(attribute='image_url'),
    'distance': fields.String(attribute='distance'),
    'provider_route' : fields.String(attribute='route'),
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


post_field_request = {
    'status' : fields.String(attribute='status')
    ,'data' : {
        'data' : fields.String(attribute='status')
    }
    ,'message' : fields.String(attribute='message')
}

request_status = {
    'status': fields.String(attribute='statusinner'),
}

get_field_request_status = {
    'status' : fields.String(attribute='status'),
    'data' : fields.Nested(request_status),
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
        lp_uid = lp_user.query.with_entities(lp_user.lp_uid).filter_by(app_id=args.app_id).first().lp_uid
        print "\n all values \n", args.encroute, trip_creation_time, routeid, lp_uid
        if db.session.query(lp_subscriber).filter_by(lp_uid=lp_uid).update({"trip_creation_time":trip_creation_time,"routeid":routeid,"encroute":args.encroute})!=0:
        #db.session.add(lp_subscriber(lp_uid[0], trip_creation_time, routeid, args.encroute))
            db.session.commit()
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
                temp['org_title'] = userd['org_title']
                temp['org_name'] = userd['org_name']
                temp['route'] = prosd['encroute']
                temp['trip_creation_time'] = prosd['trip_creation_time']
                temp['trip_elapsed_time'] = time.time()-float(prosd['trip_creation_time'])
                print "trip_elapsed_time since trip", temp['trip_elapsed_time']
                temp['display_name'] = userd['display_name']
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
        else:
            listpros['status'] = 'Failed'
            listpros['message'] = 'subscriber could not be added'
            temp = collections.defaultdict(list)
            listpros['providers'].append(temp)
        print listpros
        return listpros

class SubscriberRefresh(Resource):
    @marshal_with(get_refresh_field)
    def get(self):
        args = post_parser.parse_args()
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
            temp['org_title'] = userd['org_title']
            temp['org_name'] = userd['org_name']
            temp['route'] = prosd['encroute']
            temp['trip_creation_time'] = prosd['trip_creation_time']
            temp['trip_elapsed_time'] = time.time()-float(prosd['trip_creation_time'])
            print "trip_elapsed_time since trip", temp['trip_elapsed_time']
            temp['display_name'] = userd['display_name']
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

    @marshal_with(post_field)
    def post(self):
        pass


class SubscriberRequest(Resource):
    @marshal_with(get_field)
    def get(self):
        pass

    @marshal_with(post_field_request)
    def post(self):
        print "start subs req post"
        args = get_request_status.parse_args()
        rval = collections.defaultdict(dict)
        rval['status'] = 'error'
        rval['data'] = 'NA'
        rval['message'] = 'message'
        print args
        if bool(lp_match.query.all()) != False:
            next_id = lp_match.query.order_by(lp_match.matchid.desc()).first().matchid + 1
        else:
            next_id=1
        user = db.session.query(lp_user).filter_by(app_id = args.app_id).first()
        pquery = db.session.query(lp_provider).filter_by(lp_uid = args.pid).first()
        if user == None or pquery == None:
            return rval
        sid = user._asdict()['lp_uid']
        subs = db.session.query(lp_subscriber).filter_by(lp_uid = sid).first()
        if subs == None:
            return rval
        sroute = subs._asdict()['encroute']
        proute = pquery._asdict()['encroute']
        s_req_time = time.time()
        db.session.add(lp_match(next_id, args.pid, sid, proute, sroute, s_req_time, 0))
        db.session.commit()
        rval['status'] = 'ok'
        return rval


class SubscriberRequestStatus(Resource):
    @marshal_with(get_field_request_status)
    def get(self):
        args = get_request_status.parse_args()
        rval = collections.defaultdict(dict)
        rval['status'] = 'ok'
        rval['data']['statusinner'] = 0
        rval['message'] = 'message'
        sid = db.session.query(lp_user).filter_by(app_id = args.app_id).first()
        if sid == None:
            return rval
        sid = sid._asdict()['lp_uid']
        status = db.session.query(lp_match).filter_by(p_lp_uid = args.pid, s_lp_uid = sid).first()
        if status == None:
            return rval
        rval['data']['statusinner'] = status._asdict()['status']
        return rval

    @marshal_with(post_field)
    def post(self):
        pass
