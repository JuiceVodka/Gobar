package si.uni_lj.fri.pbd.gobar

data class MushroomDetailsModel(val id :String, val commonName :String, var latinName :String, var edibility :String, var isPsychoactive :Int, var isDiscovered :Int, var image: String):Comparable<MushroomDetailsModel>{

    override fun compareTo(other :MushroomDetailsModel): Int {
        val id1 = id
        val id2 = other.id
        return id1.compareTo(id2)
    }
}