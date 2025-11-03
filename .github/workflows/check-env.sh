#!/usr/bin/env bash
set -e  # если любая команда завершается с ошибкой — прерываем выполнение

# список обязательных переменных
required_vars=(
  FLYWAY_ADMIN_EMAIL
  FLYWAY_PASSWORD
  FLYWAY_ROLE
  FLYWAY_ENABLED
  FLYWAY_CONFIRMED
)

echo "🔍 Checking required environment variables for Flyway..."

for var in "${required_vars[@]}"; do
  if [ -z "${!var}" ]; then
    echo "❌ ERROR: Environment variable '$var' is not set."
    exit 1
  else
    echo "✅ $var=${!var}"
  fi
done

echo "✅ All required environment variables are set."