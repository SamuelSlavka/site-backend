#!/usr/bin/env python3

""" DB interaction """
import os
import psycopg2
from dotenv import load_dotenv
import json 


def connect_db():
    """ Connect to db """
    return psycopg2.connect(
        host="172.17.0.1",
        database="postgres",
        user="postgres",
        password="postgres"
    )


def create_tables():
    """ creates initial tables """
    loadFixtures = not are_fixtures_loaded();
    commands = (
        """
        CREATE TABLE IF NOT EXISTS restaurant (
            id serial PRIMARY KEY NOT NULL,
            restaurantName TEXT NOT NULL,
            restaurantEndpoint TEXT NOT NULL,
            restaurantDescription TEXT NOT NULL
        )
        """,
        """ 
        CREATE TABLE IF NOT EXISTS lunch (
            id serial PRIMARY KEY NOT NULL,
            restaurant_id INTEGER NOT NULL,
            value TEXT NOT NULL,
            FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
           )
        """)
    connection = None
    try:
        connection = connect_db()
        cursor = connection.cursor()
        # if( loadFixtures ):
            # commands += get_fixture_commands()
        print(commands)
        # create tables
        for command in commands:
            cursor.execute(command)

        # close communication with the db
        cursor.close()

        # commit the changes
        connection.commit()
    except Exception as error:
        print(error)
    finally:
        if connection is not None:
            connection.close()

def get_fixture_commands():
    commands = ()
    file = open('fixtures.json')
    data = json.load(file)["restaurants"]
    for restaurant in data:
        command = f'INSERT INTO restaurant (restaurantName, restaurantEndpoint, restaurantDescription)'
        command += f' VALUES (\'{restaurant["restaurantName"]}\', \'{restaurant["restaurantEndpoint"]}\', \'{restaurant["restaurantDescription"]}\');'
        commands += (command,)
    return commands

def are_fixtures_loaded():
    """ checks if fixtures should be loaded """
    connection = connect_db()
    cursor = connection.cursor()
    cursor.execute("select * from information_schema.tables where table_name=%s", ('restaurant',))
    return bool(cursor.rowcount)

def set_restaurant(restaurantEndpoint, restaurantDescription):
    """ save message to db """
    connection = connect_db()
    cursor = connection.cursor()

    cursor.execute(
        "INSERT INTO restaurant (restaurantEndpoint, restaurantDescription) "
        "VALUES (%s, %s)",
        (restaurantEndpoint, restaurantDescription,))
    connection.commit()
    cursor.close()
    connection.close()

def set_lunch(restaurant_id, value):
    """ save message to db """
    connection = connect_db()
    cursor = connection.cursor()

    cursor.execute(
        "INSERT INTO lunch (restaurant_id, value) "
        "VALUES (%s, %s)",
        (restaurant_id, value,))
    connection.commit()
    cursor.close()
    connection.close()

def get_restaurants():
    """ returns list of restaurants """
    connection = connect_db()
    cursor = connection.cursor()

    cursor.execute("SELECT DISTINCT id, restaurantEndpoint, restaurantDescription FROM restaurant;")
    res = []
    for row in cursor:
        res.append(row)

    cursor.close()
    connection.close()
    return res

def get_lunches(restaurantIds):
    """ returns lunches based on ids """
    connection = connect_db()
    cursor = connection.cursor()
    
    sqlRestaurntIds = tuple(restaurantIds)
    print(sqlRestaurntIds)
    cursor.execute(
        "SELECT DISTINCT id, restaurant_id, value FROM lunch WHERE restaurant_id IN %s;",
        (sqlRestaurntIds,))
    res = []
    for row in cursor:
        res.append(row)

    cursor.close()
    connection.close()
    return res