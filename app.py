#!/usr/bin/env python3

import json
import flask
from flask_cors import CORS, cross_origin
from flask import Flask, request, render_template, url_for, jsonify, redirect, flash
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.debug = True

# db config
username = "postgres"
password = "postgres"
dbname = "postgres"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SECRET_KEY'] = "postgres"
app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{username}:{password}@172.17.0.1:5432/{dbname}"
db = SQLAlchemy(app)

# flask config
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

# Create A Models For Db
class Restaurants(db.Model):
    __tablename__ = 'restaurant'
    id = db.Column(db.Integer, primary_key=True)
    restaurant_name = db.Column(db.String())
    restaurant_endpoint = db.Column(db.String())
    restaurant_description = db.Column(db.String())

    @property
    def serialized(self):
        return {
            'id': self.id,
            'restaurant_name': self.restaurant_name,
            'restaurant_endpoint': self.restaurant_endpoint,
            'restaurant_description': self.restaurant_description
        }


class Lunches(db.Model):
    __tablename__ = 'lunch'
    id = db.Column(db.Integer, primary_key=True)
    restaurant_id = db.Column(db.Integer, db.ForeignKey("restaurant.id"))
    value = db.Column(db.JSON)

    @property
    def serialized(self):
        return {
            'id': self.id,
            'restaurant_id': self.restaurant_id,
            'value': self.value
        }

db.create_all()

@app.route('/')
@app.route('/api/')
def home():
    """ home endpoint """
    return 'Hello there!'


@app.route('/api/fixtures')
def fixtures():
    """ loads fixtures into db """
    file = open('fixtures.json')
    data = json.load(file)
    for restaurant in data["restaurants"]:
        rest = Restaurants(restaurant_name=restaurant["restaurant_name"], restaurant_endpoint=restaurant[
                           "restaurant_endpoint"], restaurant_description=restaurant["restaurant_description"])
        db.session.add(rest)
        
    db.session.commit()
    for lunch in data["lunches"]:
        rest = Lunches(restaurant_id=lunch["restaurant_id"], value=lunch["value"])
        db.session.add(rest)
        
    db.session.commit()
    return 'Ok'


@app.route('/api/restaurants', methods=['GET'])
@cross_origin()
def getRestaurants():
    """ Returns restaurants """
    restaurants = Restaurants.query.all()
    result = []
    for restaurant in restaurants:
        result.append(restaurant.serialized)
    return result, 200


@app.route('/api/lunches', methods=['POST'])
@cross_origin()
def getLunches():
    """ Returns lunches by ids """
    # Lunches.query.
    req = flask.request.get_json(force=True)
    restaurantIds = req.get('restaurantIds', None)
    sqlRestaurntIds = tuple(restaurantIds)
    queryResults = db.session.query(Lunches).filter(Lunches.restaurant_id.in_(sqlRestaurntIds)).all()
    result = []
    for res in queryResults:
        result.append(res.serialized)
    return result, 200


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
