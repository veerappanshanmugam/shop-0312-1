"""Seed test data for functional tests."""
import urllib.request
import json


BASE = "http://localhost:8000"


def post(path, data):
    req = urllib.request.Request(
        BASE + path,
        json.dumps(data).encode("utf-8"),
        {"Content-Type": "application/json"},
    )
    resp = urllib.request.urlopen(req)
    return json.loads(resp.read())


def put(path, data):
    req = urllib.request.Request(
        BASE + path,
        json.dumps(data).encode("utf-8"),
        {"Content-Type": "application/json"},
        method="PUT",
    )
    resp = urllib.request.urlopen(req)
    return json.loads(resp.read())


# Create 5 users (ids 1-5)
post("/users", {"email": "ordertest1@example.com", "name": "Order Test User"})
post("/users", {"email": "ordertest2@example.com", "name": "Multi Item User"})
post("/users", {"email": "ordertest3@example.com", "name": "Product Not Found User"})
post("/users", {"email": "ordertest4@example.com", "name": "Insufficient Inv User"})
post("/users", {"email": "ordertest5@example.com", "name": "Get Order User"})

# Create 1 category (id 1)
post("/categories", {"name": "Test Category"})

# Create 5 products (ids 1-5), each auto-creates inventory with quantity=0
post("/products", {"name": "Order Test Product 1", "price": 29.99, "category_id": 1})
post("/products", {"name": "Multi Order Product A", "price": 10.0, "category_id": 1})
post("/products", {"name": "Multi Order Product B", "price": 25.5, "category_id": 1})
post("/products", {"name": "Low Stock Product", "price": 15.0, "category_id": 1})
post("/products", {"name": "Get Order Product", "price": 49.99, "category_id": 1})

# Set inventory quantities
put("/inventory/1", {"quantity": 100})
put("/inventory/2", {"quantity": 50})
put("/inventory/3", {"quantity": 50})
put("/inventory/4", {"quantity": 2})
put("/inventory/5", {"quantity": 20})

print("Seed data created successfully")
