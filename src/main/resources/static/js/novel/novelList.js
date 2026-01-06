document.addEventListener("DOMContentLoaded", () => {
  const listEl = document.getElementById("novelList");
  const categoryBtns = document.querySelectorAll(".category");

  if (!listEl) {
    console.error("div#novelList not found");
    return;
  }
  if (typeof fetchJson !== "function") {
    console.error("fetchJson not found. commons.js is not loaded?");
    return;
  }

  // 초기 로드
  loadNovels("");

  // 카테고리 버튼 클릭 바인딩
  categoryBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      categoryBtns.forEach((b) => b.classList.remove("now"));
      btn.classList.add("now");

      const type = btn.dataset.type || "";
      loadNovels(type);
    });
  });

  function loadNovels(type) {
    listEl.innerHTML = "";

    let url = "/api/novels";
    if (type) url += `?type=${encodeURIComponent(type)}`;

    fetchJson(
      url,
      { method: "GET" },
      {
        defaultErrorMessage: "소설 목록을 불러오는 중 오류가 발생했습니다.",
        toastOnSuccess: null,
        parseJson: true,
      }
    ).then((novels) => {
      if (!novels) return;

      if (!Array.isArray(novels) || novels.length === 0) {
        listEl.innerHTML = `<p class="text1 text-center">소설이 없습니다.</p>`;
        return;
      }

      const frag = document.createDocumentFragment();
      novels.forEach((novel) => frag.appendChild(buildCard(novel)));
      listEl.appendChild(frag);
    });
  }

  // 템플릿 없이 DOM 생성
  function buildCard(novel) {
    const a = document.createElement("a");
    a.className = "card novelCard flex gap-md pd-md";
    a.href = `/novel/${novel?.novelId}`;

    // cover
    const img = document.createElement("img");
    img.className = "cover";
    img.alt = "표지";
    const coverUrl =
      novel?.coverUrl && String(novel.coverUrl).trim() ? novel.coverUrl : "";
    if (coverUrl) img.src = coverUrl;
    else img.style.display = "none";

    // info wrapper
    const info = document.createElement("div");
    info.className = "info flex flex-column gap-sm";

    const meta = document.createElement("p");
    meta.className = "meta text2";

    const title = document.createElement("p");
    title.className = "title text1 bold";

    const intro = document.createElement("p");
    intro.className = "intro text2";

    // meta text 구성
    const parts = [];
    if (novel?.type) parts.push(novel.type);
    if (novel?.origin) parts.push(novel.origin);

    const epCount = novel?.episodeCount ?? 0;
    parts.push(`${epCount}화`);

    const isEnd = !!novel?.isEnd;
    parts.push(isEnd ? "완결" : "연재 중");

    meta.textContent = parts.join(" · ");
    title.textContent = novel?.name ?? "";
    intro.textContent = novel?.intro ?? "";

    info.append(meta, title, intro);
    a.append(img, info);
    return a;
  }
});