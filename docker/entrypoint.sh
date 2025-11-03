#!/bin/bash
set -e

# Проверяем обязательные переменные
: "${ADMIN_EMAIL:?Environment variable ADMIN_EMAIL must be set}"

echo "All required environment variables are set ✅"
