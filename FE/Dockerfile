# ================================================
# STAGE 1: Build Stage
# ================================================
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Install pnpm (optional, lebih cepat dari npm)
RUN npm install -g pnpm

# Copy package files
COPY package.json pnpm-lock.yaml* package-lock.json* ./

# Install dependencies
RUN if [ -f pnpm-lock.yaml ]; then pnpm install --frozen-lockfile; \
    elif [ -f package-lock.json ]; then npm ci; \
    else npm install; fi

# Copy source code
COPY . .

# Build application
RUN npm run build

# ================================================
# STAGE 2: Production Stage
# ================================================
FROM nginx:alpine

# Install required packages
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built application from builder stage
COPY --from=builder --chown=nextjs:nodejs /app/.output/public /usr/share/nginx/html

# Copy startup script
COPY docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh && chown nextjs:nodejs /entrypoint.sh

# Create upload directory (jika diperlukan)
RUN mkdir -p /usr/share/nginx/html/uploads && \
    chown -R nextjs:nodejs /usr/share/nginx/html

# Switch to non-root user
USER nextjs

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:80/ || exit 1

# Custom entrypoint
ENTRYPOINT ["/entrypoint.sh"]