FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm ci

ARG NEXT_PUBLIC_PORTONE_V2_STORE_ID
ARG NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY
ENV NEXT_PUBLIC_PORTONE_V2_STORE_ID=${NEXT_PUBLIC_PORTONE_V2_STORE_ID}
ENV NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY=${NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY}

COPY . .

RUN npm run build -- --no-lint

FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV production

COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 3000

CMD ["npm", "start"]