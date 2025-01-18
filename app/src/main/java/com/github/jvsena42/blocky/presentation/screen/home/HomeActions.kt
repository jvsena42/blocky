package com.github.jvsena42.blocky.presentation.screen.home

sealed interface HomeActions {
    data object OnClickSearch : HomeActions
}