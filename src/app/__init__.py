from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS, cross_origin

db = SQLAlchemy()

def create_app():
    """Construct the core application."""
    app = Flask(__name__, instance_relative_config=False)
    app.config.from_object('config.Config')
    app.config['CORS_HEADERS'] = 'Content-Type'
    
    CORS(app)
    db.init_app(app)

    with app.app_context():
        from . import routes  # Import routes
        db.create_all()  # Create sql tables for our data models
        return app