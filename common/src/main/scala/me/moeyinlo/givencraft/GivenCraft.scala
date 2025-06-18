package me.moeyinlo.givencraft

import org.slf4j.{Logger, LoggerFactory}

private trait GivenCraft

object GivenCraft:

    def logger: Logger = LoggerFactory.getLogger(classOf[GivenCraft])

    def initialize(): Unit =
        logger.info("Initializing GivenCraft")