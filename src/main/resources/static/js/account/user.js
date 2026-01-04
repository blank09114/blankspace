// 회원 목록 불러오기
async function loadUserList(page = 0)
{
    const list = document.querySelector(".userList");
    if (!list) return;

    const data = await fetchJson(
        `/api/user/list?page=${page}&size=10`,
        { method: "GET" },
        { defaultErrorMessage: "회원 목록을 불러오지 못했습니다." }
    );

    if (!data) return;

    renderUserList(data.content);

    const pagination = document.querySelector("[data-pagination]");
    if (pagination) renderPagination(pagination, data.number, data.totalPages, loadUserList);
}

// 회원 카드 렌더링
function renderUserList(users)
{
    const list = document.querySelector(".userList");
    if (!list) return;

    list.innerHTML = "";

    if (!users || users.length === 0)
    {
        const empty = document.createElement("div");
        empty.className = "card pd-md flex flex-column align-center";
        empty.innerHTML = `<span class="text3 black2">표시할 회원이 없습니다.</span>`;
        list.appendChild(empty);
        return;
    }

    users.forEach(u =>
    {
        const a = document.createElement("a");
        a.className = "card pd-md user flex flex-column gap-sm";
        a.href = `/user/${encodeURIComponent(u.userId)}`;
        a.dataset.userId = u.userId;

        const blockText = u.isBlocked ? "차단됨" : "정상";
        const reasonText = u.isBlocked ? (u.blockedReason || "-") : "-";
        const joinDateText = u.userDate ? formatDate(u.userDate) : "-";

        a.innerHTML =
        `
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">닉네임</p>
                <p class="title3Text">${escapeHtml(u.userName || "-")}</p>
            </div>
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">ID</p>
                <p class="title3Text">${escapeHtml(u.userId || "-")}</p>
            </div>
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">MAIL</p>
                <p class="title3Text">${escapeHtml(u.userMail || "-")}</p>
            </div>
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">가입일</p>
                <p class="title3Text">${joinDateText}</p>
            </div>
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">상태</p>
                <p class="title3Text">${blockText}</p>
            </div>
            <div class="userInfo flex flex-column-mov gap-sm">
                <p class="title3Text bold">차단 사유</p>
                <p class="title3Text">${escapeHtml(reasonText)}</p>
            </div>
        `;

        list.appendChild(a);
    });
}

// XSS 방지용(메일/닉네임/사유 문자열 안전 처리)
function escapeHtml(str)
{
    return String(str ?? "")
    .replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;").replaceAll("'", "&#039;");
}

// 닉네임 폼 토글
function nameToggle(btn)
{
    const userInfo = btn.closest(".userInfo");
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    const form = userInfo.querySelector('form[name="nameForm"]');
    const input = form.querySelector('input[name="name"]');

    const isEditing = form.style.display === "flex";

    if (isEditing)
    {
        // 보기 모드로 전환
        form.style.display = "none";
        nameText.style.display = "block";
        btn.textContent = "변경";
    }
    else
    {
        // 수정 모드로 전환
        input.value = nameText.textContent.trim();
        nameText.style.display = "none";
        form.style.display = "flex";
        btn.textContent = "취소";
    }
}

// 닉네임 변경
async function editName(btn)
{
    const form = btn.closest('form[name="nameForm"]');
    const userInfo = btn.closest('.userInfo');
    const input = form ? form.querySelector('input[name="name"]') : null;

    if (!input || !userInfo)
    {
        console.error('닉네임 폼 요소를 찾지 못함', { btn, form, userInfo, input });
        showToast("요청 처리 중 문제가 발생했습니다.");
        return;
    }

    const newName = input.value.trim();
    if (!newName) { showToast("닉네임을 입력해주세요."); return; }

    const targetUserId = document.body.dataset.userId;
    if (!targetUserId) { showToast("요청 처리 중 문제가 발생했습니다."); return; }

    const data = await fetchJson(`/api/user/${encodeURIComponent(targetUserId)}/name`,
    {
        method: "PATCH",
        body: JSON.stringify({ userName: newName })
    },
    {
        defaultErrorMessage: "닉네임 변경에 실패했습니다.",
        toastOnSuccess: "닉네임이 변경되었습니다."
    });

    if (!data) return;

    // 화면 갱신
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    nameText.textContent = data.userName;
    form.style.display = "none";
    nameText.style.display = "block";

    const toggleBtn = userInfo.querySelector('button.btn[onclick^="nameToggle"]');
    if (toggleBtn) toggleBtn.textContent = "변경";
}

// 차단
async function toggleBlock(btn)
{
    const targetUserId = document.body.dataset.userId;
    if (!targetUserId) { showToast("요청 처리 중 문제가 발생했습니다."); return; }

    const isUnblock = btn.textContent.includes("해제");
    let body = null;

    if (!isUnblock)
    {
        const reason = prompt("차단 사유를 입력해주세요. (20자 이내)");
        if (reason === null) return;
        body = { reason: reason.trim() };
    }

    const data = await fetchJson
    (
        `/api/user/${encodeURIComponent(targetUserId)}/block`,
        { method: "PATCH", body: body ? JSON.stringify(body) : null },
        {
            defaultErrorMessage: "처리에 실패했습니다.",
            toastOnSuccess: isUnblock ? "차단이 해제되었습니다." : "사용자를 차단했습니다."
        }
    );

    if (!data) return;
    location.reload();
}

// 로그인 기록 불러오기
async function loadLoginLogs(page = 0)
{
    const list = document.getElementById("loginLogList");
    if (!list) return;

    const userId = document.body.dataset.userId;
    if (!userId) return;

    const data = await fetchJson(
        `/api/user/${encodeURIComponent(userId)}/login-logs?page=${page}`,
        { method: "GET" },
        { defaultErrorMessage: "로그인 기록을 불러오지 못했습니다." }
    );

    if (!data) return;

    renderLoginLogs(data.content);
    const pagination = list.closest(".activityMenu").querySelector("[data-pagination]");

    renderPagination( pagination, data.number, data.totalPages, loadLoginLogs);
}

// 렌더링
function renderLoginLogs(logs)
{
    const list = document.getElementById("loginLogList");
    if (!list) return;

    list.querySelectorAll(".activity:not(.head)").forEach(e => e.remove());

    if (logs.length === 0)
    {
        const empty = document.createElement("div");
        empty.className = "activity flex flex-column align-center";
        empty.innerHTML = `<span class="text3 black2">로그인 기록이 없습니다.</span>`;
        list.appendChild(empty);
        return;
    }

    logs.forEach(log =>
    {
        const item = document.createElement("div");
        item.className = "activity flex flex-column align-center";

        const statusText = log.logoutDate? `${formatDate(log.logoutDate)} 로그아웃됨`: "세션 유지 중";

        item.innerHTML =
        `
            <span class="text3 black2">${statusText}</span>
            <div class="activityBottom flex align-center">
                <span class="text1">${log.loginIp} (${log.loginRegion})</span>
                <span class="text2 date">${formatDate(log.loginDate)}</span>
            </div>
        `;

        list.appendChild(item);
    });
}

// 이벤트 리스너
window.addEventListener("load", () =>
{
    // 로그인 기록 불러오기
    if (document.getElementById("loginLogList")) { loadLoginLogs(0); }

    // 회원 목록 불러오기
    if (document.querySelector(".userList")) { loadUserList(0); }
});