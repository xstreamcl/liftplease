from flask_restful import fields, marshal_with, reqparse, Resource, inputs
from db import db, lp_user,lp_provider,lp_subscriber,lp_match

# To get info on all users 

# Request parsers
parser = reqparse.RequestParser()

## parser copy
get_parser = parser.copy()
post_parser = parser.copy()

# Response fields
user_field = {
    'uniq_lp_id': fields.Integer(attribute='lp_uid'),
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
    'prov_id': fields.Integer(attribute='lp_uid'),
    'route_id': fields.String(attribute='routeid'),
    'encroute': fields.String(attribute='encroute'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
}

subscriber_field = {
    'subs_id': fields.Integer(attribute='lp_uid'),
    'route_id': fields.String(attribute='routeid'),
    'encroute': fields.String(attribute='encroute'),
    'trip_creation_time': fields.String(attribute='trip_creation_time'),
}

match_field = {
    'match_id': fields.Integer(attribute='lp_uid'),
    'provider_id' : fields.Integer(attribute='p_lp_uid'),
    'subscriber_id' : fields.Integer(attribute='s_lp_uid'),
    'prov_routeid': fields.String(attribute='p_routeid'),
    'subsc_routeid': fields.String(attribute='s_routeid'),
    'match status': fields.String(attribute='status'),
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

class All_Subscriber(Resource):
    @marshal_with(subscriber_field)
    def get(self):
        subscriber = lp_subscriber.query.all()
        return subscriber

class All_Match(Resource):
    @marshal_with(match_field)
    def get(self):
        match = lp_match.query.all()
        return match

