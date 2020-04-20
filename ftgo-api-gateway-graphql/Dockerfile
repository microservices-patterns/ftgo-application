FROM node:9.11.2-alpine
COPY package.json .
COPY package-lock.json .
RUN npm install
RUN npm config set unsafe-perm true && npm install -g typescript
COPY tsconfig.json .
ADD src ./src
RUN npm run build
CMD npm run start
