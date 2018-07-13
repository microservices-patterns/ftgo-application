FROM node:9.11.2-alpine
COPY package.json .
RUN npm install
COPY tsconfig.json .
ADD src ./src
RUN npm run build
CMD npm run start
