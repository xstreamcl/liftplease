from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_provider, lp_user
import uuid

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
post_parser.add_argument( 'g_id', dest='g_id', type=int, required=True )
post_parser.add_argument( 'eta', dest='departtime', type=str, required=True )
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
    'eta': fields.String(attribute='departtime'),
}

post_field = {
    'id': fields.Integer(attribute='lp_uid'),
    'name': fields.String(attribute='display_name'),
    'key': fields.String(attribute='app_id'),
}

# Resource class

class Provider(Resource):
    @marshal_with(get_field)
    def get(self):
        args = get_parser.parse_args()
        # if app_id is valid
        user = None
        provider = None
        if lp_user.query.filter_by(app_id=args.app_id).first() != None:
            user = lp_user.query.get(args.lp_uid)
            provider = lp_provider.query.get(args.lp_uid)
        return user, provider

    @marshal_with(post_field)
    def post(self):
        args = post_parser.parse_args()
        routeid = str(uuid.uuid4())
        lp_uid = lp_user.query.with_entities(lp_user.lp_uid).filter_by(g_id=args.g_id).first().lp_uid
        print "this is lp uid", lp_uid
        db.session.add(lp_provider(lp_uid, args.departtime, routeid, args.encroute))
        db.session.commit()
        print len(lp_provider.query.all())
        user_provider = lp_provider.query.get(lp_uid)
        return user_provider