package si.uni_lj.fri.pbd.gobar


data class MushroomLocationModel(val id :String, val commonName :String, var latinName :String, var lat :String, var long :String) :Comparable<MushroomLocationModel>{

    override fun compareTo(other :MushroomLocationModel): Int {
        val id1 = id
        val id2 = other.id
        return id1.compareTo(id2)
    }
}