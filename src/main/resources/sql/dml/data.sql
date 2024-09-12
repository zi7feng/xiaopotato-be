USE TAPI;

-- Mock data for InterfaceInfo table
INSERT INTO InterfaceInfo (name, description, url, request_params, request_header, response_header, status, method, user_id, create_time, update_time, is_delete) VALUES
  ('GetUserDetails', 'Fetches user details by ID', 'https://api.example.com/users/details', '{ "userId": "integer" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'GET', 1, NOW(), NOW(), 0),
  ('CreateOrder', 'Creates a new order', 'https://api.example.com/orders/create', '{ "orderData": "object" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'POST', 2, NOW(), NOW(), 0),
  ('UpdateOrderStatus', 'Updates the status of an order', 'https://api.example.com/orders/update', '{ "orderId": "integer", "status": "string" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'PUT', 3, NOW(), NOW(), 0),
  ('DeleteOrder', 'Deletes an order by ID', 'https://api.example.com/orders/delete', '{ "orderId": "integer" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 0, 'DELETE', 4, NOW(), NOW(), 0),
  ('GetProductList', 'Retrieves the list of all products', 'https://api.example.com/products/list', '{}', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'GET', 5, NOW(), NOW(), 0),
  ('CreateProduct', 'Adds a new product', 'https://api.example.com/products/create', '{ "productData": "object" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'POST', 1, NOW(), NOW(), 0),
  ('UpdateProductInfo', 'Updates product information', 'https://api.example.com/products/update', '{ "productId": "integer", "productData": "object" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'PUT', 2, NOW(), NOW(), 0),
  ('DeleteProduct', 'Removes a product by ID', 'https://api.example.com/products/delete', '{ "productId": "integer" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 0, 'DELETE', 3, NOW(), NOW(), 0),
  ('GetOrderHistory', 'Fetches order history for a user', 'https://api.example.com/orders/history', '{ "userId": "integer" }', '{ "Authorization": "Bearer token" }', '{ "Content-Type": "application/json" }', 1, 'GET', 4, NOW(), NOW(), 0),
  ('UserLogin', 'Authenticates a user login', 'https://api.example.com/users/login', '{ "username": "string", "password": "string" }', '{ "Content-Type": "application/x-www-form-urlencoded" }', '{ "Set-Cookie": "session_id" }', 1, 'POST', 5, NOW(), NOW(), 0);
