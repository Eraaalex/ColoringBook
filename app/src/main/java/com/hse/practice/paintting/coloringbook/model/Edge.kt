package com.hse.practice.paintting.coloringbook.model

data class Edge(val v0: Point, val v1: Point) {
    fun equals(edge: Edge): Boolean {
        return (this.v0 == edge.v0 && this.v1 == edge.v1) || (this.v0 == edge.v1 && this.v1 == edge.v0)
    }
}
