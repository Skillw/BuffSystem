package com.skillw.buffsystem.internal.feature.compat.pouvoir.container

import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.container.IContainer
import com.skillw.pouvoir.internal.feature.container.Container

@AutoRegister
object BuffContainer : IContainer by Container(BuffSystem)