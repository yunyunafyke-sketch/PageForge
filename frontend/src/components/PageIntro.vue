<script setup lang="ts">
defineProps<{ title: string; description: string; tag?: string }>()
</script>

<template>
  <section class="page-intro">
    <div>
      <span v-if="tag" class="intro-tag">{{ tag }}</span>
      <h2>{{ title }}</h2>
      <p>{{ description }}</p>
    </div>
    <div class="page-actions"><slot /></div>
  </section>
</template>
