<template>
  <div class="app-container">
    <Navbar />
    <main class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  padding: 20px 0;
  background-color: #f5f5f5;
}
.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
