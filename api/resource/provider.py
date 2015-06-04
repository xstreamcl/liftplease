from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_provider, lp_user, lp_subscriber, lp_match
import uuid
import collections

# !the final call on abstracting this and including it into a configuration file has to be made, so the code looks cleaner!

# Request parsers

parser = reqparse.RequestParser()
#enable when key format is done :
#parser.add_argument( 'key', dest='app_id', type=inputs.regex('^.{10}$'), required=True, help='Application id' )

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()
get_refresh_parser = parser.copy()
post_refresh_parser = parser.copy()

## get argumetns for provider class
get_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_parser.add_argument( 'id', dest='lp_uid', type=int, required=True, help='The user\'s id' )
## post arguments for provider calss
post_parser.add_argument( 'key', dest='app_id', type=str, required=True )
post_parser.add_argument( 'route', dest='encroute', type=str, required=True )

## get argugments for provider refresh class
get_refresh_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_refresh_parser.add_argument( 'route', dest='encroute', type=int, required=True, help='The user\'s id' )
## post arguments for provider refresh class 
post_refresh_parser.add_argument( 'key', dest='app_id', type=str, required=True )
post_refresh_parser.add_argument( 'id', dest='lp_uid', type=int, required=True )

# Response fields for provider class

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
    'eta': fields.String(attribute='departtime'),
}

post_field = {
    'status': fields.String(attribute='status'),
    'message' : fields.String(attribute='message'),
    'data' : fields.String(attribute='data')
}

# Response fields for provider refresh class

get_refresh_subfield = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'org_title' : fields.String(attribute='org_title'),
    'org_name' : fields.String(attribute='org_name'),
    'waiting_since' : fields.String(attribute='waiting_since'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
    'image': fields.String(attribute='image_url'),
    'subscriber_route' : fields.String(attribute='route'),
}

get_refresh_data_field = {
    'subscribers': fields.List(fields.Nested(get_refresh_subfield))   
}

get_refresh_field = {
    'status' : fields.String(attribute='status'),
    'data' : get_refresh_data_field,
    'message' : fields.String(attribute='message'),
}

post_refresh_field = {
    'status': fields.String(attribute='status'),
    'message' : fields.String(attribute='message'),
    'data' : fields.String(attribute='data')
}

#Provider resource class
class Provider(Resource):
    @marshal_with(get_field)
    def get(self):
        '''
        Outputs user info along with provider info i.e. route, trip_creation_time
        '''
        args = get_parser.parse_args()
        # if app_id is valid
        user = None
        provider = None
        if lp_user.query.filter_by(app_id=args.app_id).first() != None:
            if lp_provider.query.filter_by(lp_uid=args.lp_uid).first() != None:
                user = lp_user.query.get(args.lp_uid)
                provider = lp_provider.query.get(args.lp_uid)
                user.eta=provider.departtime
                user.route=provider.encroute
                print user.eta,user.route
            else:
                return user
        return user

    @marshal_with(post_field)
    def post(self):
        '''
        Registers a user as a Provider in provider table. 
        '''
        args = post_parser.parse_args()
        routeid = str(uuid.uuid4())
        trip_creation_time = time.time()
        post_reply = {}
        try:
            lp_uid = lp_user.query.filter_by(app_id=args.app_id).first().lp_uid #this call should never fail.
            db.session.add(lp_provider(lp_uid, trip_creation_time, routeid, args.encroute))
            db.session.commit()
            post_reply['status'] = 'OK'
            post_reply['message'] = 'Provider added'
            post_reply['data'] = 'null'
        except:
            post_reply['status'] = 'Failed'
            post_reply['message'] = 'db exception'
            post_reply['data'] = 'null'
        return post_reply

#Provider refresh resource class
class Provider_Refresh(Resource):
    @marshal_with(get_refresh_field)
    def get(self):
        '''
        Get list of all subscribers interested in riding with a provider from match table
        '''
        args = get_refresh_parser.parse_args()
        p_lp_uid = lp_user.query.filter_by(app_id=args.app_id).first.lp_uid
        listsubs = collections.defaultdict(list)
        try:
            for subs, match, user in db.session.query(lp_subscriber, lp_match, lp_user)\
            .join(lp_match, lp_subscriber.lp_uid == lp_match.s_lp_uid).filter_by(p_lp_uid=p_lp_uid,status=0)\
            .join(lp_user, lp_subscriber.lp_uid == lp_user.lp_uid)\
            .all():
                subsd = subs._asdict()
                userd = user._asdict()
                temp = collections.defaultdict(list)
                temp['lp_uid'] = userd['lp_uid']
                temp['org_title'] = userd['org_title']
                temp['org_name'] = userd['org_name']
                temp['route'] = subsd['encroute']
                temp['trip_creation_time'] = subsd['trip_creation_time']
                temp['waiting_since'] = time.time()-float(subsd['trip_creation_time'])
                print "waiting_since since trip", temp['waiting_since']
                temp['display_name'] = userd['display_name']
                temp['image_url'] = userd['image_url']
                listsubs['subscribers'].append(temp)
                listsubs['status'] = 'OK'
                listsubs['message'] = 'list of subscribers'
        except:
            listsubs['subscribers']='None'
            listsubs['status'] = 'OK'
            listsubs['message'] = 'No subscribers'
        print listsubs
        return listsubs


    @marshal_with(post_refresh_field)
    def post(sef):
        '''
        Accept lift request from a subscriber 
        '''
        args = post_refresh_parser.parse_args()
        p_lp_uid = lp_user.query.filter_by(app_id=args.app_id).first.lp_uid
        try:
            lp_match.query.filter_by(p_lp_uid=p_lp_uid,s_lp_uid=args.lp_uid).update({'status':1})
            db.session.commit()
            post_reply['status'] = 'OK'
            post_reply['message'] = 'lift request from subscriber acccepted'
            post_reply['data'] = 'null'
        except:
            post_reply['status'] = 'Failed'
            post_reply['message'] = 'lift request cant be approved'
            post_reply['data'] = 'null'
        return post_reply

