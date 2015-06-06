from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_provider, lp_user, lp_subscriber, lp_match
import uuid
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
get_refresh_parser = parser.copy()
post_request_parser = parser.copy()

## get argumetns for provider class
get_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='App id of user making request' )
get_parser.add_argument( 'id', dest='lp_uid', type=int, required=True, help='The user\'s id' )

## post arguments for provider calss
post_parser.add_argument( 'key', dest='app_id', type=str, required=True )
post_parser.add_argument( 'route', dest='encroute', type=str, required=True )

## get argugments for provider refresh class
get_refresh_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='App id of user making request' )
get_refresh_parser.add_argument( 'route', dest='encroute', type=str, required=True, help='The user\'s id' )

## post arguments for provider request class 
post_request_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='App id of user making request') 
post_request_parser.add_argument( 'subscriber', dest='lp_uid', type=int, required=True, help='lp_uid of the subscriber, the user wants to offer lift')

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
    'phone' : fields.String(attribute='phone'),
    'org_title' : fields.String(attribute='org_title'),
    'org_name' : fields.String(attribute='org_name'),
    'waiting_since' : fields.String(attribute='waiting_since'),
    'req_creation_time': fields.String(attribute='req_creation_time'),
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

# Response fields for provider request class

post_request_field = {
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
        #try:
        lp_uid = lp_user.query.filter_by(app_id=args.app_id).first().lp_uid #this call should never fail.
        if db.session.query(lp_provider).filter_by(lp_uid=lp_uid).update({"trip_creation_time":trip_creation_time,"routeid":routeid,"encroute":args.encroute})!=0:
            #db.session.add(lp_provider(lp_uid, trip_creation_time, routeid, args.encroute))
            db.session.commit()
            post_reply['status'] = 'OK'
            post_reply['message'] = 'Provider added'
            post_reply['data'] = 'null'
        else:
            post_reply['status'] = 'Failed'
            post_reply['message'] = 'provider could not be updated'
            post_reply['data'] = 'null'
        #except:
        #    post_reply['status'] = 'Failed'
        #    post_reply['message'] = 'db exception'
        #    post_reply['data'] = 'null'
        return post_reply

#Provider refresh resource class
class Provider_Refresh(Resource):
    @marshal_with(get_refresh_field)
    def get(self):
        '''
        Get list of all subscribers interested in riding with a provider from match table
        '''
        args = get_refresh_parser.parse_args()
        p_lp_uid = lp_user.query.filter_by(app_id=args.app_id).first().lp_uid
        print "provider unique lp id" ,p_lp_uid
        listsubs = collections.defaultdict(list)
        print "before try"
#        try:
        print "insdie try"
        for subs, match, user in db.session.query(lp_subscriber, lp_match, lp_user).join(lp_match, lp_subscriber.lp_uid == lp_match.s_lp_uid).filter_by(p_lp_uid=p_lp_uid,status=0).join(lp_user, lp_subscriber.lp_uid == lp_user.lp_uid).all():
            print "inside for"
            subsd = subs._asdict()
            matchd = match._asdict()
            userd = user._asdict()
#            print "subscriber", subsd
 #           print "user info", userd
            print "\n from match \n", matchd
            print "\n request creation time", matchd['s_req_time']
            temp = collections.defaultdict(list)
            temp['lp_uid'] = userd['lp_uid']
            temp['org_title'] = userd['org_title']
            temp['org_name'] = userd['org_name']
            temp['route'] = subsd['encroute']
            temp['req_creation_time'] = matchd['s_req_time']
            temp['waiting_since'] = time.time()-float(matchd['s_req_time'])
            print "waiting_since since trip", temp['waiting_since']
            temp['display_name'] = userd['display_name']
            temp['image_url'] = userd['image_url']
            listsubs['subscribers'].append(temp)
            listsubs['status'] = 'OK'
            listsubs['message'] = 'list of subscribers'
            print "join output in for", listsubs
 #       except:
  #          print "inside except"
   #         listsubs['subscribers']='None'
    #        listsubs['status'] = 'Failed'
     #       listsubs['message'] = 'gotta catch the exception'
        if len(listsubs)!=0:
            return listsubs
        else:
            print "inside except"
            listsubs['subscribers']='None'
            listsubs['status'] = 'OK'
            listsubs['message'] = 'No subscribers'
        print "before return", listsubs, len(listsubs)
        return listsubs

    def post(self):
        pass

class Provider_Request(Resource):
    def get(self):
        pass

    @marshal_with(post_request_field)
    def post(sef):
        '''
        Accept lift request from a subscriber 
        '''
        args = post_request_parser.parse_args()
        p_lp_uid = lp_user.query.filter_by(app_id=args.app_id).first().lp_uid
        post_reply={}
        try:
            if lp_match.query.filter_by(p_lp_uid=p_lp_uid,s_lp_uid=args.lp_uid,status=0).update({'status':1})!=0:
                db.session.commit() #doubt if I should do this?
                post_reply['status'] = 'OK'
                post_reply['message'] = 'lift request from subscriber acccepted'
                post_reply['data'] = 'null'
            else:
                post_reply['status'] = 'Failed'
                post_reply['message'] = 'The lift request can\'t be accepted right now'
                post_reply['data'] = 'null'
        except:
            post_reply['status'] = 'Error'
            post_reply['message'] = 'some error'
            post_reply['data'] = 'null'
        return post_reply

