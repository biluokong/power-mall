<template>
  <transition name="fade">
    <router-view></router-view>
  </transition>
</template>

<!--
1.vue项目 你需要看得懂发请求
2.vue项目你要看得懂，会改一部分
3.vue项目 你需要会运行起来（改配置文件）
-->


<script>
export default {
  computed: {
    key () {
      return this.$route.path + Math.random()
    }
  }
}
</script>
