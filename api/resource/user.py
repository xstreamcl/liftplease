from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_user, lp_provider, lp_subscriber
import uuid
import sys

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
    'key': fields.String(attribute='app_id')
    }
    ,'messsage' : fields.String(attribute='messsage')
    
}

# Resource class

class User(Resource):
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
                user={}
                user['app_id'] = app_id
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
