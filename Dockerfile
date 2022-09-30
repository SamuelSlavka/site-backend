FROM python:latest

COPY ./requirements.txt /app/requirements.txt
WORKDIR /app
RUN pip3 install -r requirements.txt
COPY * /app


CMD [ "python3", "app.py" ]
# CMD gunicorn app:app -b 0.0.0.0:5000