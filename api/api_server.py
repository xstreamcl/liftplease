from flask import Flask
from flask_restful import Api
from resource.user import User
from resource.provider import Provider
from resource.user import User
from resource.all_user import All_User, All_Provider


app = Flask(__name__)
api = Api(app)

# Resource/Routes/EndPoints

api.add_resource(User, '/user')
api.add_resource(Provider, '/provider')
api.add_resource(All_Provider, '/all/p')
api.add_resource(All_User, '/all/u')

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')
#    app.run(host='0.0.0.0')
