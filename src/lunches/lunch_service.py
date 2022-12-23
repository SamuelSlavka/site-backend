from src.app.models import db, Restaurants, Lunches

class LunchService():
  def getAll():
    lunches = Lunches.query.all()
    result = []
    for res in lunches:
        result.append(res.serialized)
    return result

  def getLunchesForRestaurnats(sqlRestaurntIds):
    queryResults = db.session.query(Lunches).filter(
        Lunches.restaurant_id.in_(sqlRestaurntIds)).all()
    result = []
    for res in queryResults:
        result.append(res.serialized)
    return result

  def setLunchFixtures(data):
    lunches = Lunches.query.all()
    restaurants = Restaurants.query.all()
    if(not lunches and restaurants):
      restId = restaurants[0].id
      for lunch in data["lunches"]:
          rest = Lunches(restaurant_id=restId, value=lunch["value"])
          db.session.add(rest)
          db.session.commit()