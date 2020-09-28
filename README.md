# redis-distributed-api-rate-limiter
This repository contains a working sample application to implement distributed API rate limiting using Redis along with support of using configurable environmental properties for parameterization of the DistributedRateLimiter Annotation.

## Motivation

Implement API rate limiting while calling external APIs to avoid overloading aforementioned external APIs


This can be used to implement API rate limiting while calling external APIs using Sliding Window with Counters stored in Redis Hash across all sentinels using the MULTI, INCR and EXPIRE commands. It uses SessionCallback to execute two commands in single atomic operation (Redis Transaction) - INCR and EXPIRE. The RedisTemplate does not provide any surity that two consecutive operations will run on the same connection, SessionCallback is a reliable way of handling transactions.
This can be used to acquire distributed locks through Redis over a specific key across all sentinels using the SETNX command implementation of Redis and uses Java lambdas to combine multiple statements of releasing the distributed lock and converts this process into an atomic operation and ensures correctness of the unlocking operation by not deleting someone else's lock.

Reference : https://redislabs.com/redis-best-practices/basic-rate-limiting/ | https://redis.io/topics/data-types
