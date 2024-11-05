FROM node:20.18.0 AS builder

WORKDIR /EnergyManagementSystemFrontend
COPY package*.json ./
RUN npm install -g npm@10.2.3
COPY . .
RUN npm run build

CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port","3000"]