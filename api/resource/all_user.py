from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_user,lp_provider,lp_subscriber

# To get info on all users 

# Request parsers
parser = reqparse.RequestParser()

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()

# Response fields
user_field = {
    'uniq id': fields.Integer(attribute='lp_uid'),
    'device_id': fields.String(attribute='device_id'),
    'g_id': fields.String(attribute='g_id'),
    'app_id': fields.String(attribute='app_id'),
    'name': fields.String(attribute='display_name'),
    'gender': fields.String(attribute='gender'),
    'email': fields.String(attribute='email'),
    'image_uri': fields.String(attribute='image_url'),
    'org_title': fields.String(attribute='org_title'),
    'org name': fields.String(attribute='org_name'),
    'about': fields.String(attribute='about_me'),
}

provider_field = {
    'id': fields.Integer(attribute='lp_uid'),
    'route': fields.String(attribute='routeid'),
    'route': fields.String(attribute='encroute'),
    'eta': fields.String(attribute='departtime'),
}

class All_User(Resource):
    @marshal_with(user_field)
    def get(self):
        # if app_id is valid
        user = lp_user.query.all()
        return user

class All_Provider(Resource):
    @marshal_with(provider_field)
    def get(self):
        provider = lp_provider.query.all()
        return provider
