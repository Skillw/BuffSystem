package com.skillw.buffsystem.api.event

import com.skillw.buffsystem.api.data.BuffData
import taboolib.platform.type.BukkitProxyEvent

/**
 * @className BuffEvent
 *
 * @author Glom
 * @date 2023/1/24 14:07 Copyright 2023 user. All rights reserved.
 */
class BuffEvent {
    /**
     * Tick
     *
     * @constructor Create empty Tick
     */
    class Tick {
        /**
         * Pre
         *
         * @constructor Create empty Pre
         * @property data
         */
        class Pre(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }

        /**
         * Post
         *
         * @constructor Create empty Post
         * @property data
         */
        class Post(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }
    }

    /**
     * Condition
     *
     * @constructor Create empty Tick
     */
    class Condition {
        /**
         * Pre
         *
         * @constructor Create empty Pre
         * @property data
         */
        class Pre(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }

        /**
         * Post
         *
         * @param pass
         * @constructor Create empty Post
         * @property data
         */
        class Post(val data: BuffData, var pass: Boolean) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }
    }

    /**
     * Realize
     *
     * @constructor Create empty Realize
     */
    class Realize {
        /**
         * Pre
         *
         * @constructor Create empty Pre
         * @property data
         */
        class Pre(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }

        /**
         * Post
         *
         * @constructor Create empty Post
         * @property data
         */
        class Post(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }
    }

    /**
     * Release
     *
     * @constructor Create empty Release
     */
    class Release {
        /**
         * Pre
         *
         * @constructor Create empty Pre
         * @property data
         */
        class Pre(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }

        /**
         * Post
         *
         * @constructor Create empty Post
         * @property data
         */
        class Post(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }
    }

    /**
     * Description
     *
     * @constructor Create empty Tick
     */
    class Description {
        /**
         * Pre
         *
         * @constructor Create empty Pre
         * @property data
         */
        class Pre(val data: BuffData) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }

        /**
         * Post
         *
         * @constructor Create empty Post
         * @property data
         */
        class Post(val data: BuffData, var description: MutableList<String>) : BukkitProxyEvent() {
            override val allowCancelled: Boolean = false
        }
    }
}