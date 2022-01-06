.DEFAULT_GOAL := help
help:
	@grep -E '^[a-zA-Z-]+:.*?## .*$$' Makefile | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "%s - %s\n", $$1, $$2}'
.PHONY: help

run-all-tests: ## Run integration and unit tests
	@docker-compose -f docker-compose.dev.yml down --volumes && docker-compose -f docker-compose.dev.yml up -d --build && docker-compose -f docker-compose.dev.yml exec d_reporter lein test

up-dev: ## Run integration and unit tests
	@docker-compose -f docker-compose.dev.yml down --volumes && docker-compose -f docker-compose.dev.yml up -d --build
