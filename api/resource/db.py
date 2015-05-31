from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///lp.db'
db = SQLAlchemy(app)


class lp_user(db.Model):
    lp_uid = db.Column(db.Integer, primary_key=True, autoincrement=True)
    app_id = db.Column(db.Text)
    g_id = db.Column(db.Integer)
    display_name = db.Column(db.Text)
    gender = db.Column(db.Text)
    email = db.Column(db.Text)
    image_url = db.Column(db.Text)
    about_me = db.Column(db.Text)
    occupation = db.Column(db.Text)
    org_type = db.Column(db.Text)
    org_name = db.Column(db.Text)
    org_title = db.Column(db.Text)
    org_dept = db.Column(db.Text)

    def __init__(self, lp_uid, app_id, g_id, display_name, gender, email, image_url, about_me, occupation, org_type, org_name, org_title, org_dept):
        self.lp_uid = lp_uid
        self.app_id = app_id
        self.g_id = g_id
        self.display_name = display_name
        self.gender = gender
        self.email = email
        self.image_url = image_url
        self.about_me = about_me
        self.occupation = occupation
        self.org_type = org_type
        self.org_name = org_name
        self.org_title = org_title
        self.org_dept = org_dept


class lp_provider(db.Model):
    lp_uid = db.Column(db.Integer, primary_key=True)
    departtime = db.Column(db.DateTime)
    routeid = db.Column(db.Integer)
    encroute = db.Column(db.LargeBinary)

    def __init__(self, lp_uid, departtime, routeid, encroute):
        self.lp_uid = lp_uid
        self.departtime = departtime
        self.routeid = routeid
        self.encroute = encroute

class lp_subscriber(db.Model):
    lp_uid = db.Column(db.Integer, primary_key=True)
    departtime = db.Column(db.DateTime)
    routeid = db.Column(db.Integer)
    encroute = db.Column(db.LargeBinary)

    def __init__(self, lp_uid, departtime, routeid, encroute):
        self.lp_uid = lp_uid
        self.departtime = departtime
        self.routeid = routeid
        self.encroute = encroute


class lp_match(db.Model):
    matchid = db.Column(db.Integer, primary_key=True)
    p_lp_uid = db.Column(db.Integer)
    s_lp_uid = db.Column(db.Integer)
    dropoffset = db.Column(db.Float)

    def __init__(self, matchid, p_lp_uid, s_lp_uid, dropoffset):
        self.matchid = matchid
        self.p_lp_uid = p_lp_uid
        self.s_lp_uid = s_lp_uid
        self.dropoffset = dropoffset


#me = lp_provider(102, datetime.now(), 100, 'IJDFY()*_#!^%&^@FMCfvb$IUO$&(UYIO&(SDFJD*(Uhdkjf')
#db.session.add(me)
#db.session.commit()


#missing = lp_provider.query.first()
#print missing.lp_uid

