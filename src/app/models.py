"""Data models."""
from enum import Enum
from . import db

class Restaurants(db.Model):
    __tablename__ = 'restaurant'
    id = db.Column(db.Integer, primary_key=True)
    restaurant_name = db.Column(db.String())
    restaurant_endpoint = db.Column(db.String())
    restaurant_description = db.Column(db.String())

    def __init__(self, restaurant_name, restaurant_endpoint, restaurant_description):
        self.restaurant_name = restaurant_name
        self.restaurant_endpoint = restaurant_endpoint
        self.restaurant_description = restaurant_description

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

    def __init__(self, restaurant_id, value):
        self.restaurant_id = restaurant_id
        self.value = value

    @property
    def serialized(self):
        return {
            'id': self.id,
            'restaurant_id': self.restaurant_id,
            'value': self.value
        }

