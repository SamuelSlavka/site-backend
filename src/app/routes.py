from flask import request, render_template, make_response
from datetime import datetime as dt
from flask import current_app as app
from .models import db, Restaurants, Lunches
from flask_cors import cross_origin
from src.lunches.lunch_service import LunchService
from src.restaurants.restaurant_service import RestaurantService
from src.lunches.lunch_utils import getLunch, getToday

import json
import flask

@app.route('/')
@app.route('/api/')
def home():
    """ home endpoint """
    return 'Hello there!'


@app.route('/api/all_restaurants', methods=['GET'])
@cross_origin()
def getAllRestaurants():
    """ Returns all restaurants """
    restaurants = RestaurantService.getAll()
    return restaurants, 200


@app.route('/api/all_lunches', methods=['GET'])
@cross_origin()
def getAllLunches():
    """ Returns all lunches """
    lunches = LunchService.getAll()
    return lunches, 200


@app.route('/api/lunches', methods=['POST'])
@cross_origin()
def getLunches():
    """ Returns lunches by ids """
    req = flask.request.get_json(force=True)
    restaurantIds = req.get('restaurantIds', None)
    result = LunchService.getLunchesForRestaurnats(tuple(restaurantIds))
    return result, 200


@app.route('/api/fixtures')
def fixtures():
    """ Loads fixtures into db """
    file = open('fixtures.json')
    data = json.load(file)
    
    RestaurantService.setRestaurantFixtures(data)
    LunchService.setLunchFixtures(data)
    return 'Ok', 200


@app.route('/api/update', methods=['GET'])
@cross_origin()
def updateLunches():
    """Update list of lunches """
    restaurants = db.session.query(Restaurants).all()
    try:
        num_rows_deleted = db.session.query(Lunches).delete()
        db.session.commit()
    except:
        db.session.rollback()
    for restaurant in restaurants:
        try:
            lunch = getLunch(restaurant.restaurant_name,
                             restaurant.restaurant_endpoint)
            newLunch = Lunches(restaurant_id=restaurant.id, value=lunch)
            db.session.add(newLunch)
            db.session.commit()
        except:
            db.session.rollback()
    return {'deleted': num_rows_deleted, 'restaurants': len(restaurants), 'day': getToday()}


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
