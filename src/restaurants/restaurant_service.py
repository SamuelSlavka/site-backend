from src.app.models import db, Restaurants, Lunches

class RestaurantService():
  def getAll():
    restaurants = Lunches.query.all()
    result = []
    for res in restaurants:
        result.append(res.serialized)
    return result

  def setRestaurantFixtures(data):
    for restaurant in data["restaurants"]:
        old = Restaurants.query.where(
            Restaurants.restaurant_endpoint == restaurant["restaurant_endpoint"]).all()
        if(not old):
            rest = Restaurants(restaurant_name=restaurant["restaurant_name"], restaurant_endpoint=restaurant[
                "restaurant_endpoint"], restaurant_description=restaurant["restaurant_description"])
            db.session.add(rest)
            db.session.commit()