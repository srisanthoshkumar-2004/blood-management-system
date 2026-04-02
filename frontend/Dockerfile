# Build Stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Build for production
RUN npm run build

# Production Stage - Nginx to serve static files
FROM nginx:alpine
# Copy the built files from the build stage to Nginx content directory
COPY --from=build /app/dist /usr/share/nginx/html
# Copy custom Nginx config for React Routing (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
