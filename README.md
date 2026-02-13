<h1 align="center">
SB-ECommerce-Web-Application
</h1>


Tech Stack
- 

- FrontEnd : React.js
- Backend : SpringBoot (Java)



Created the backend part of the application, which includes managing Categories, Products, Address, Cart, CartItem, OrderItem, Payment, User, Order, etc. Leveraging Spring data JPA to communicate with database efficiently, and Spring security by adding custom filter to validate/manage the JWT token attached to the request.
-

Implemented cookie based JWT token configured to authenticate the user requests. 
- 


- Containerized the whole backend using docker, created the docker image and pushed it to DockerHub 

![img_27.png](img_27.png)

- The Docker image for backend is uploaded to the DockerHub

![img_26.png](img_26.png)

- Used Docker compose to run this backend from DockerHub's image and the postgres database containers together and deployed the same on AWS EC2 instance.

- Dockerising the application and running it using docker compose : 
`docker compose up --build`

![img.png](img.png)
![img_1.png](img_1.png)

- We can see the containers are created

![img_2.png](img_2.png)
- The port 5433 of the system is mapped to port 5432 of the docker container on which the PostgreSQL is hosted

- Setting the server on pgadmin to connect with the postgres database running from the container

![img_3.png](img_3.png)
- Added this database running on container, to the pgadmin (NOTE : We have used the port 5433 here, since the container's 5432 default postgres port is manually mapped by us to the 5433 port of the system)


- We can see the database is connected, and these are the few user's we added manually to the database

![img_5.png](img_5.png)


- Now we are signing through the sign-in endpoint for this admin user. We can see the Jwt token generated for them.

![img_7.png](img_7.png)


- Now with this Jwt token user can carry out all the api requests
- We can see the cookie is set with jwt token, this is passed with every request

![img_8.png](img_8.png)

- Created the category with the same jwt token set in the cookie

![img_9.png](img_9.png)

- We can verify that the category of the product is added in the database

![img_10.png](img_10.png)

- We can verify that the entry in categories table is being created by getting interactive shell of the running postgres container and querying sql inside it

![img_11.png](img_11.png)


Now Configuring AWS EC2 for deployment
- 

- Created EC2 instance on AWS

![img_12.png](img_12.png)
- We have set the inbound traffic in the security group of ec2 instance to allow inbound traffic on port 8080, and on port 5433

- On port 8080 our app is running and on port 5433 of ec2 instance database container is exposed, so we could connect to it using pgadmin and see the database

![img_13.png](img_13.png)

- We ssh into the ec2 instance and install docker and docker compose and git in it
- Cloning the project repository there

![img_15.png](img_15.png)
- Navigated to the repository folder
- Added the .env file
- And running command `sudo docker compose up`
  which will run both the containers on ec2

![img_16.png](img_16.png)

- Now we try to access the backend using the public api of EC2 server, and try to place an order using defined backend api's through postman


- Signing in with admin user



![img_17.png](img_17.png)

- Created new category "Clothing"


![img_18.png](img_18.png)
- Adding product with this category


![img_19.png](img_19.png)
- Added quantity = 2 of that product to the cart

![img_20.png](img_20.png)
- Added the address of the user


![img_21.png](img_21.png)
- Now placing the order

![img_22.png](img_22.png)
![img_23.png](img_23.png)
- The order was placed successfully on the application running on docker containers using docker compose on EC2 instance AWS.


- We could verify the same using pgadmin, connecting to this database using the public ip of the instance and port 5543 of ec2 instance which is mapped to port 5542 on which postgres service is running inside the container

![img_25.png](img_25.png)

- We can see the order entry is created in the orders table of the ecommerce database

![img_24.png](img_24.png)



<h3>
Successfully containerized the spring boot application using docker, and pushed the docker image to the docker hub and using this image from docker hub in the docker compose file. And deployed the same on the AWS EC2 instance
</h3>

<h4>
Thank you for checking out my project :)
</h4>