from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_user, lp_provider, lp_subscriber
import uuid
import sys
import collections
import unirest

# !the final call on abstracting this and including it into a configuration file has to be made, so the code looks cleaner!

# Request parsers

parser = reqparse.RequestParser()
#enable when key format is done :
#parser.add_argument( 'key', dest='app_id', type=inputs.regex('^.{10}$'), required=True, help='Application id' )

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()
post_cancel_parser = parser.copy()
get_cancel_parser = parser.copy()
post_phone_parser = parser.copy()

## get for User Class
get_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
get_parser.add_argument( 'id', dest='lp_uid', type=int, required=True, help='The user\'s id' )

## post for User Class
post_parser.add_argument( 'device_id', dest='device_id', type=str, required=True )
post_parser.add_argument( 'g_id', dest='g_id', type=str, required=True )
post_parser.add_argument( 'phone', dest='phone', type=str, required=False )
post_parser.add_argument( 'name', dest='display_name', type=str, required=False )
post_parser.add_argument( 'gender', dest='gender', type=str, required=False )
post_parser.add_argument( 'email', dest='email', type=str, required=False )
post_parser.add_argument( 'image_uri', dest='image_url', type=str, required=False )
post_parser.add_argument( 'about', dest='about_me', type=str, required=False )
post_parser.add_argument( 'org_name', dest='org_name', type=str, required=False )
post_parser.add_argument( 'org_title', dest='org_title', type=str, required=False )

## post request for User Trip Cancel
post_cancel_parser.add_argument('key', dest='app_id', type=str, required=True, help='Application id' )

## post phone for user 
post_phone_parser.add_argument( 'key', dest='app_id', type=str, required=True, help='Application id' )
post_phone_parser.add_argument( 'phone', dest='phone', type=str, required=True, help='User phone number')

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
    'status': fields.String(attribute='status'),
}

post_field = {
    'status' : fields.String(attribute='status')
    ,'data' : {
    'key': fields.String(attribute='app_id'),
    'phone' : fields.String(attribute='phone'),
    }
    ,'messsage' : fields.String(attribute='messsage')

}

# Response user cancel trip 
cancel_post_field = {
    'status' : fields.String(attribute='status'),
    'data' : fields.String(attribute='app_id'),
    'messsage' : fields.String(attribute='messsage'),
}

# update phone response
phone_status = {
    'status': fields.String(attribute='statusinner'),
}

post_phone_field = {
    'status' : fields.String(attribute='status'),
    'data' : fields.Nested(phone_status),
    'message' : fields.String(attribute='message'),
}

# Resource classes

class User(Resource):
    """
    user's main Resource class
    """

    @marshal_with(get_field)
    def get(self):
        args = get_parser.parse_args()
        # if app_id is valid
        user = None
        if lp_user.query.filter_by(app_id=args.app_id).first() != None:
            user = db.session.query(lp_user).get(args.lp_uid)._asdict()
            if lp_provider.query.get(args.lp_uid) != None:
                user['status'] = 'provider'
            elif lp_subscriber.query.get(args.lp_uid) != None:
                user['status'] = 'subscriber'
        return user

    @marshal_with(post_field)
    def post(self):
        args = post_parser.parse_args()
        user = None
        app_id = str(uuid.uuid4())
        if bool(lp_user.query.all()) != False:
            try:
                app_id = lp_user.query.filter_by(device_id=args.device_id,g_id=args.g_id).first().app_id
                phone = lp_user.query.filter_by(app_id=app_id).first().phone
                user={}
                user['app_id'] = app_id
                user['phone'] = phone
                user['status'] = 'OK'
                user['messsage'] = 'Already registered chutiye'
                print user
                return user
            except:
                next_id = lp_user.query.order_by(lp_user.lp_uid.desc()).first().lp_uid + 1
        else:
            next_id = 1
        print next_id
        try:
            db.session.add(lp_user(next_id, args.device_id, args.g_id, app_id, args.phone, args.display_name, args.gender, args.email, args.image_url, args.about_me, args.org_name, args.org_title))
            db.session.commit()
        except:
            allerror = sys.exc_info()[0]
            print "catch the exact exception chutiye"
            user['status'] = 'Failed'
            user['messsage'] = 'Failed to register-catch the exact exception chutiye'
        print len(lp_user.query.all())
        user={}
        user = lp_user.query.get(next_id)
        user.status = 'OK'
        user.messsage = 'successfully registered'
        return user

class Cancel_Trip(Resource):
    """
    Cancel a user's existing trip. Right now deleting user trips in Provider/Subscriber/Match db created in last 30mins.
    """
    def get(self):
        pass

    @marshal_with(cancel_post_field)
    def post(self):
        args = post_cancel_parser.parse_args()
        lp_uid = lp_user.query.filter_by(app_id=args.app_id).first().lp_uid

class UpdatePhone(Resource):
    """Update Phone number of a user"""
    def get(self):
        pass

    @marshal_with(post_phone_field)
    def post(self):
        args = post_phone_parser.parse_args()
        post_reply = collections.defaultdict(dict)
        sUser='acedip'
        sPswd='fuckoff'
        sSid='DryDay'
        sms='Happy Lift Giving/Taking. Successfully registered on LiftPlease.'
        #try:
        print "\n phone \n", args.phone
        print "\n key - appdi\n", args.app_id
        if lp_user.query.filter_by(app_id=args.app_id).update({'phone':args.phone})!=0:
            db.session.commit() #doubt if I should do this?
            post_reply['status'] = 'OK'
            post_reply['message'] = 'phone number updated'
            post_reply['data']['statusinner'] = 'null'
            response = unirest.get("http://cloud.smsindiahub.in/vendorsms/pushsms.aspx?user="+sUser+"&password="+sPswd+"&msisdn="+args.phone+"&sid="+sSid+"&msg="+sms+"&fl=0")
        else:
            post_reply['status'] = 'Failed'
            post_reply['message'] = 'phone number could not be updated'
            post_reply['data']['statusinner'] = 'null'
        #except:
         #   post_reply['status'] = 'Error'
          #  post_reply['message'] = 'some exception'
          #  post_reply['data']['statusinner'] = 'null'
        return post_reply

