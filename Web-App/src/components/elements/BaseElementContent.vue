<template>

</template>
<script>
    import colormap from "../../services/colormap.js"

    export default {
        props: ["id", "parent", "color"],
        methods: {
            willLeave() {
                console.log("Will Leave not implemented!")
            }
        }
    }
</script>