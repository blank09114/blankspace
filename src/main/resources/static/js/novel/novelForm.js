// 1차/2차 변경 처리
function onTypeChange(selectEl)
{
    const originEl = document.querySelector('[name="originInput"]');
    const isSecondary = selectEl.value === "2차";

    if (!originEl) return;

    originEl.readOnly = !isSecondary;
    if (!isSecondary) originEl.value = "";
}

// 소설 등록
function subNovel()
{
    const typeEl   = document.querySelector('[name="typeSelect"]');
    const originEl = document.querySelector('[name="originInput"]');
    const titleEl  = document.querySelector('[name="titleInput"]');
    const introEl  = document.querySelector('[name="introInput"]');

    const type = typeEl ? typeEl.value : "";

    if (type === "2차")
    {
        if (!checkEl(originEl, "원작")) return;
    }

    if (!checkEl(titleEl, "제목")) return;
    if (!checkEl(introEl, "소개글")) return;

    const title   = getValueEl(titleEl);
    const content = getValueEl(introEl);
    const origin  = getValueEl(originEl);

    // 등록은 추후 구현
}

// 회차 등록
function subEpisode()
{
    const contentEl = document.querySelector('[name="contentInput"]');

    if (!checkEl(contentEl, "회차 내용")) return;

    const content = getValueEl(contentEl);

    // 등록은 추후 구현
}

// 설정 등록
function subWorld()
{
    const titleEl   = document.querySelector('[name="titleInput"]');
    const contentEl = document.querySelector('[name="contentInput"]');

    if (!checkEl(titleEl, "제목")) return;
    if (!checkEl(contentEl, "본문")) return;

    const title   = getValueEl(titleEl);
    const content = getValueEl(contentEl);

    // 등록은 추후 구현
}