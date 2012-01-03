#!/bin/bash
export MEMCACHED_SASL_PWDB=`pwd`/memcached-sasl-db
export SASL_CONF_PATH=`pwd`/memcached.conf
memcached -vvv -S