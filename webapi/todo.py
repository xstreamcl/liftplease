from flask import Flask
from flask_restful import reqparse, abort, Api, Resource
import sqlite3
from flask import g

"""
DATABASE = './sample.db'

def get_db():
	db = getattr(g, '_database', None)
	if db is None:
		db = g._database = connect_to_database()
	return db

def close_connection(exception):
	db = getattr(g, '_database', None)
	if db is not None:
		db.close()
"""

app = Flask(__name__)
api = Api(app)

USERS = {
	'user1': {'username': 'first'},
	'user2': {'username': 'second'}
}

TAKER={}
GIVER={}

def abort_if_user_doesnt_exist(user_id):
	if user_id not in USERS:
		abort(404, message="User {} doesn't exist".format(user_id))

parser = reqparse.RequestParser()
parser.add_argument('username', type=str)
parser.add_argument('user_id', type=str)
parser.add_argument('state', type=str)
parser.add_argument('eta', type=str)
parser.add_argument('route', type=str)


# shows a single user and lets you delete a user
class User(Resource):
	def get(self, user_id):
		abort_if_user_doesnt_exist(user_id)
		return USERS[user_id]

	def delete(self, user_id):
		abort_if_user_doesnt_exist(user_id)
		del USERS[user_id]
		return '', 204

	def put(self, user_id):
		args = parser.parse_args()
		username = {'username': args['username']}
		USERS[user_id] = username
		return username, 201

	def post(self,user_id):
		args = parser.parse_args()
		if args['state']=="taker":
			eta={'eta':args['eta']}
			route={'route':args['route']}
			TAKER[user_id] = eta,route
			return TAKER

# shows a list of all USERS, and lets you POST to add new usernames
class UserList(Resource):
	def get(self):
		return USERS

	def post(self):
		args = parser.parse_args()
		if args['user_id'] in USERS:
			abort(404, message="User {} exist".format(args['user_id']))
		else:
			user_id = int(max(USERS.keys()).lstrip('user')) + 1
			user_id = 'user%i' % user_id
			USERS[user_id] = {'username': args['username']}
			return USERS[user_id], 201

## Actually setup the Api resource routing here

api.add_resource(UserList, '/users')
api.add_resource(User, '/users/<user_id>')

#curl http://localhost:5000/users
#curl http://localhost:5000/users -d "username=third&user_id=user2" -X POST -v
#curl http://localhost:5000/users/user1
#curl http://localhost:5000/users/user1 -d "state=taker&eta=time&route=xy"

#curl http://whenisdryday.in:5000/users -d "username=third&user_id=user2" -X POST -v

if __name__ == '__main__':
	app.run(debug=True)
	#app.run(debug=True,host=0.0.0.0)
	