<script setup lang="ts">
import { computed } from 'vue'
import { useData, withBase } from 'vitepress'

const props = defineProps<{ section: string }>()
const { theme } = useData()
const chapters = computed(
  () => (theme.value.chapters?.[props.section] ?? []) as { title: string; link: string | null; status: string }[],
)
</script>

<template>
  <ul class="chapter-list">
    <li v-for="c in chapters" :key="c.title">
      <a v-if="c.link" :href="withBase(c.link)">{{ c.title }}</a>
      <span v-else class="pending">{{ c.title }} <em>검토 중</em></span>
    </li>
  </ul>
</template>

<style scoped>
.chapter-list {
  list-style: none;
  padding-left: 0;
}
.chapter-list li {
  padding: 6px 0;
  border-bottom: 1px solid var(--vp-c-divider);
}
.pending {
  color: var(--vp-c-text-3);
}
.pending em {
  margin-left: 8px;
  font-size: 12px;
  font-style: normal;
  padding: 1px 6px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 4px;
}
</style>
