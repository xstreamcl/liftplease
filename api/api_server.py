from flask import Flask
from flask_restful import Api
from resource.user import User

app = Flask(__name__)
api = Api(app)


# Resource/Routes/EndPoints

api.add_resource(User, '/user')


if __name__ == '__main__':
    app.run(debug=True)
    app.run(host='0.0.0.0')