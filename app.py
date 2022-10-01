#!/usr/bin/env python3

import os
import sys
from flask import Flask
from db import *

app = Flask(__name__)
app.debug = True

create_tables()

@app.route('/')
@app.route('/api/')
def home():
    return 'Hello there!'

@app.route('/api/restaurants/', methods=['GET'])
def getRestaurants():
    """ Deployed restaurants """
    return get_restaurants(), 200

@app.route('/api/lunches/', methods=['GET'])
def getLunches():
    """ Deployed lunches """
    # req = Flask.request.get_json(force=True)
    # restaurantIds = req.get('restaurantIds', None)
    return get_lunches(['1','2']), 200


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)

