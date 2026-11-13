import fs from 'node:fs'
import path from 'node:path'
import matter from 'gray-matter'
import { defineConfig } from 'vitepress'

// 발행 규칙: 챕터(NN-제목.md)는 머리말의 status가 reviewed일 때만 사이트에 나간다.
// 로컬에서 초안까지 보려면 DEVELOG_INCLUDE_DRAFTS=1 (npm run dev가 자동으로 켠다).
const docsDir = path.resolve(process.cwd(), 'docs')
const includeDrafts = process.env.DEVELOG_INCLUDE_DRAFTS === '1'
const CHAPTER = /^(\d{2})-.+\.md$/

type Chapter = { title: string; link: string | null; status: string }
type Section = { dir: string; title: string; order: number; chapters: Chapter[] }

function readSections(): { sections: Section[]; excluded: string[] } {
  const sections: Section[] = []
  const excluded: string[] = []
  for (const dir of fs.readdirSync(docsDir).sort()) {
    const abs = path.join(docsDir, dir)
    const index = path.join(abs, 'index.md')
    if (dir.startsWith('.') || dir === 'public' || !fs.existsSync(index)) continue
    if (!fs.statSync(abs).isDirectory()) continue
    const meta = matter(fs.readFileSync(index, 'utf8')).data
    const chapters: Chapter[] = []
    for (const file of fs.readdirSync(abs).filter((f) => CHAPTER.test(f)).sort()) {
      const data = matter(fs.readFileSync(path.join(abs, file), 'utf8')).data
      const status = String(data.status ?? 'draft')
      const published = status === 'reviewed' || includeDrafts
      if (!published) excluded.push(`${dir}/${file}`)
      chapters.push({
        title: `${file.slice(0, 2)}. ${data.title ?? file.replace(/\.md$/, '')}`,
        link: published ? `/${dir}/${file.replace(/\.md$/, '')}` : null,
        status,
      })
    }
    sections.push({ dir, title: String(meta.title ?? dir), order: Number(meta.order ?? 99), chapters })
  }
  sections.sort((a, b) => a.order - b.order || a.dir.localeCompare(b.dir))
  return { sections, excluded }
}

const { sections, excluded } = readSections()

export default defineConfig({
  lang: 'ko-KR',
  title: 'develog',
  description: 'TypeScript 개발자의 눈으로 정리하는 언어별 학습 기록',
  base: '/develog/',
  cleanUrls: true,
  lastUpdated: true,
  srcExclude: excluded,
  themeConfig: {
    nav: [
      ...sections.map((s) => ({ text: s.title, link: `/${s.dir}/` })),
      { text: '만든 방식', link: '/about' },
      { text: '포트폴리오', link: 'https://grangbelrlurain.github.io/' },
    ],
    sidebar: Object.fromEntries(
      sections.map((s) => [
        `/${s.dir}/`,
        [
          {
            text: s.title,
            items: [
              { text: '개요', link: `/${s.dir}/` },
              ...s.chapters.filter((c) => c.link).map((c) => ({ text: c.title, link: c.link! })),
            ],
          },
        ],
      ]),
    ),
    // ChapterList 컴포넌트가 읽는 목록 (발행 전 챕터는 link가 null)
    // @ts-expect-error 기본 테마 타입에 없는 사용자 정의 필드
    chapters: Object.fromEntries(sections.map((s) => [s.dir, s.chapters])),
    search: { provider: 'local' },
    outline: { label: '이 페이지에서', level: [2, 3] },
    docFooter: { prev: '이전', next: '다음' },
    lastUpdated: { text: '마지막 수정' },
    socialLinks: [{ icon: 'github', link: 'https://github.com/GrangbelrLurain/develog' }],
  },
})
