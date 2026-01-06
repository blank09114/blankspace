// 입력값 가져오기
function getValue(inputId)
{
    const input = document.getElementById(inputId);
    return input.value.trim();
}
function getValueEl(inputEl) { return (inputEl?.value ?? "").trim(); }

// 입력 확인
function check(inputId, inputName)
{
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (value === "")
    {
        showToast(`${inputName}을(를) 입력하세요.`);
        input.focus();
        return false;
    }

    return true;
}
function checkEl(inputEl, inputName)
{
    const value = getValueEl(inputEl);

    if (value === "")
    {
        showToast(`${inputName}을(를) 입력하세요.`);
        inputEl?.focus();
        return false;
    }

    return true;
}

// 길이 제한 체크
function checkMaxLength(inputId, inputName, max)
{
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (value.length > max)
    {
        showToast(`${inputName}은(는) ${max}자 이내로 작성하세요.`);
        input.focus();
        return false;
    }

    return true;
}
function checkMaxLengthEl(inputEl, inputName, max)
{
    const value = getValueEl(inputEl);

    if (value.length > max)
    {
        showToast(`${inputName}은(는) ${max}자 이내로 작성하세요.`);
        inputEl?.focus();
        return false;
    }

    return true;
}

// 썸네일 업로드 컨트롤
function imgInputControll(dir = "thumb", urlInputName = "thumbnailUrl")
{
    const fileInput = document.querySelector(`[name="imgInput"]`);
    const nameInput = document.querySelector(`[name="imgNameInput"]`);
    const urlInput  = document.querySelector(`[name="${urlInputName}"]`)
    || document.querySelector(`[name="thumbnailUrl"]`);

    if (!fileInput) return;

    fileInput.value = "";
    fileInput.click();

    fileInput.onchange = async () =>
    {
        const file = fileInput.files?.[0];
        if (!file) return;

        // 파일명 표시
        if (nameInput) nameInput.value = file.name;

        // 클라이언트 선검증
        const allowedExt = [".png", ".jpg", ".jpeg", ".gif"];
        const lowerName = (file.name || "").toLowerCase();
        const okExt = allowedExt.some(ext => lowerName.endsWith(ext));
        if (!okExt)
        {
            showToast("썸네일은 png/jpg/jpeg/gif만 업로드 가능합니다.");
            fileInput.value = "";
            if (urlInput) urlInput.value = "";
            return;
        }

        const max = 10 * 1024 * 1024;
        if (file.size > max)
        {
            showToast("썸네일은 최대 10MB까지 업로드 가능합니다.");
            fileInput.value = "";
            if (urlInput) urlInput.value = "";
            return;
        }

        // 업로드
        try
        {
            showToast("썸네일 업로드 중...");

            const formData = new FormData();
            formData.append("file", file, file.name);

            const res = await fetch("/api/upload?dir=thumb", { method: "POST", body: formData });

            if (!res.ok)
            {
                const msg = await res.text().catch(() => "");
                throw new Error(msg || `Upload failed (HTTP ${res.status})`);
            }

            const data = await res.json();
            const url = data?.url;

            if (!url) throw new Error("No url in response");
            if (urlInput) urlInput.value = url;

            showToast("썸네일 업로드 완료!");
        }
        catch (e)
        {
            console.error(e);
            showToast("썸네일 업로드 실패");
            if (urlInput) urlInput.value = "";
        }
    };
}

// TinyMCE 공통 에디터
window.RichEditor = (() =>
{
    function isReady() { return !!window.tinymce; }

    function ensureId(el)
    {
        if (el.id && el.id.trim() !== "") return el.id;
        const id = `editor_${Math.random().toString(36).slice(2, 10)}`;
        el.id = id;
        return id;
    }

    function getSelector(textarea)
    {
        const id = ensureId(textarea);
        return `#${CSS.escape(id)}`;
    }

    function initOne(textarea)
    {
        if (!textarea) return;
        if (textarea.dataset.editorInited === "1") return;
        if (!isReady()) return;

        const selector = getSelector(textarea);
        const height = parseInt(textarea.dataset.editorHeight || "600", 10);

        tinymce.init({
            selector: `#${textarea.id}`,
            height: 600,

            menubar: false,
            branding: false,
            promotion: false,
            license_key: "gpl",

            skin: "oxide",
            content_css: "default",

            plugins: "lists link image table code codesample",
            toolbar:
            [
                "undo redo | fontsize | bold italic underline strikethrough | superscript subscript | forecolor backcolor | alignleft aligncenter alignright alignjustify",
                "bullist numlist | hr | table | link image | codesample | removeformat | code"
            ].join(" | "),

            font_size_formats: "24px 20px 18px 16px 14px 12px",
            fontsize_default: "16px",

            content_style: "body { font-size: 16px; }",

            images_upload_handler: (blobInfo, progress) => new Promise((resolve, reject) =>
            {
                const xhr = new XMLHttpRequest();

                xhr.open("POST", "/api/upload?dir=editor");
                xhr.responseType = "json";

                xhr.upload.onprogress = (e) => { if (e.lengthComputable) progress((e.loaded / e.total) * 100); };

                xhr.onload = () =>
                {
                    if (xhr.status !== 200) return reject("Upload failed");

                    const url = xhr.response?.url;
                    if (!url) return reject("No url in response");

                    resolve(url);
                };

                xhr.onerror = () => reject("Network error");

                const formData = new FormData();
                formData.append("file", blobInfo.blob(), blobInfo.filename());
                xhr.send(formData);
            }),
        });

        textarea.dataset.editorInited = "1";
    }

    function initAll(root = document)
    {
        if (!isReady()) return;
        const list = root.querySelectorAll("textarea[data-tinymce]");
        list.forEach(initOne);
    }

    function sync()
    {
        if (!isReady()) return;
        tinymce.triggerSave();
    }

    function bindAutoSync()
    {
        document.addEventListener("submit", (e) =>
        {
            const formEl = e.target;
            if (!(formEl instanceof HTMLFormElement)) return;

            const hasEditor = !!formEl.querySelector("textarea[data-tinymce]");
            if (!hasEditor) return;

            sync();
        }, true);
    }

    return { initAll, sync, bindAutoSync };
})();

document.addEventListener("DOMContentLoaded", () =>
{
    window.RichEditor.initAll();
    window.RichEditor.bindAutoSync();
});