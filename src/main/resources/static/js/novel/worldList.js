document.addEventListener("DOMContentLoaded", () =>
{
    const pageEl = document.getElementById("worldListPage");
    const listEl = document.getElementById("worldList");
    const categoryBtns = document.querySelectorAll(".category");

    if (!pageEl) { console.error("#worldListPage not found"); return; }
    if (!listEl) { console.error("#worldList not found"); return; }
    if (typeof fetchJson !== "function")
    { console.error("fetchJson not found. commons.js is not loaded?"); return; }

    const novelId = pageEl.dataset.novelId;
    if (!novelId)
    {
        console.error("data-novel-id is missing on #worldListPage");
        return;
    }

    // 초기 로드
    loadWorlds("");

    // 카테고리 버튼 클릭 바인딩
    categoryBtns.forEach((btn) =>
    {
        btn.addEventListener("click", () =>
        {
            categoryBtns.forEach((b) => b.classList.remove("now"));
            btn.classList.add("now");

            const category = btn.dataset.category || "";
            loadWorlds(category);
        });
    });

    function loadWorlds(category)
    {
        listEl.innerHTML = "";

        let url = `/api/novels/${encodeURIComponent(novelId)}/worlds`;
        if (category) url += `?category=${encodeURIComponent(category)}`;

        fetchJson(
            url,
            { method: "GET" },
            {
                defaultErrorMessage: "설정 목록을 불러오는 중 오류가 발생했습니다.",
                toastOnSuccess: null,
                parseJson: true,
            }
        ).then((worlds) =>
        {
            if (!worlds) return;

            if (!Array.isArray(worlds) || worlds.length === 0)
            {
                listEl.innerHTML = `<p class="text1 text-center">설정이 없습니다.</p>`;
                return;
            }

            const frag = document.createDocumentFragment();
            worlds.forEach((w) => frag.appendChild(buildRow(w)));
            listEl.appendChild(frag);
        });
    }

    // DOM 생성
    function buildRow(w)
    {
        const a = document.createElement("a");
        a.className = "episode";
        a.href = `/novel/${novelId}/world/${w?.id}`;

        const pCategory = document.createElement("p");
        pCategory.className = "text1 black2";
        pCategory.textContent = w?.category ?? "";

        const pTitle = document.createElement("p");
        pTitle.className = "text1";
        pTitle.textContent = w?.name ?? "";

        a.append(pCategory, pTitle);
        return a;
    }
});