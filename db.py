""" DB interaction """
import psycopg2
from .constants import *



def connect_db():
    """ Connect to db """
    return psycopg2.connect(
        host=HOST,
        database=DATABASE,
        user=USER,
        password=PASSWORD
    )


def create_tables():
    """ creates initial tables """
    commands = (
        """
        CREATE TABLE IF NOT EXISTS restaurant (
           id INTEGER PRIMARY KEY NOT NULL AUTOINCREMENT,
           restaurantEndpoint TEXT NOT NULL,
           restaurantDescription TEXT NOT NULL
        )
        """,
        """ 
        CREATE TABLE IF NOT EXISTS lunch (
           id INTEGER PRIMARY KEY NOT NULL AUTOINCREMENT,
           restaurant_id INTEGER NOT NULL,
           value TEXT NOT NULL,
           FOREIGN KEY (author_id) REFERENCES user (id)
           )
        """)
    conn = None
    try:
        conn = connect_db()
        cur = conn.cursor()

        # create tables
        for command in commands:
            cur.execute(command)

        # close communication with the db
        cur.close()

        # commit the changes
        conn.commit()

    except Exception as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()


def set_restaurant(restaurantEndpoint, restaurantDescription):
    """ save message to db """
    conn = connect_db()
    cur = conn.cursor()

    cur.execute(
        "INSERT INTO restaurant (restaurantEndpoint, restaurantDescription) "
        "VALUES (%s, %s)",
        (restaurantEndpoint, restaurantDescription,))
    conn.commit()
    cur.close()
    conn.close()

def set_lunch(restaurant_id, value):
    """ save message to db """
    conn = connect_db()
    cur = conn.cursor()

    cur.execute(
        "INSERT INTO lunch (restaurant_id, value) "
        "VALUES (%s, %s)",
        (restaurant_id, value,))
    conn.commit()
    cur.close()
    conn.close()

def get_restaurants():
    """ returns list of restaurants """
    conn = connect_db()
    cur = conn.cursor()

    cur.execute("SELECT DISTINCT id, restaurantEndpoint, restaurantDescription FROM restaurant;")
    res = []
    for row in cur:
        res.append(row)

    cur.close()
    conn.close()
    return res

def get_lunches(restaurantIds):
    """ returns lunches based on ids """
    conn = connect_db()
    cur = conn.cursor()

    cur.execute(
        "SELECT DISTINCT id, restaurant_id, value FROM lunch WHERE restaurant_id && %s;",
        (restaurantIds,))
    res = []
    for row in cur:
        res.append(row)

    cur.close()
    conn.close()
    return res