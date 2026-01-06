// 1차/2차 변경 처리
function onTypeChange(selectEl)
{
    const originEl = document.querySelector('[name="origin"]');
    const isSecondary = selectEl.value === "2차";

    if (!originEl) return;

    originEl.readOnly = !isSecondary;
    if (!isSecondary) originEl.value = "";
}

// 소설 등록
function subNovel()
{
    const formEl   = document.getElementById("postForm");
    const typeEl   = document.querySelector('[name="type"]');
    const originEl = document.querySelector('[name="origin"]');
    const nameEl   = document.querySelector('[name="name"]');
    const introEl  = document.querySelector('[name="intro"]');

    const type = typeEl ? typeEl.value : "";

    if (type === "2차") { if (!checkEl(originEl, "원작")) return; }

    if (!checkEl(nameEl, "제목")) return;
    if (!checkEl(introEl, "소개글")) return;

    // 길이 제한
    if (getValueEl(nameEl).length > 20) { showToast("제목은 20자 이내로 작성하세요."); nameEl.focus(); return; }
    if (type === "2차" && getValueEl(originEl).length > 20) { showToast("원작은 20자 이내로 작성하세요."); originEl.focus(); return; }

    HTMLFormElement.prototype.submit.call(formEl);
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