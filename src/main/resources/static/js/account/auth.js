// 정규식/메시지
const REGEX =
{
    name: /^.{2,10}$/,
    mail: /^[a-zA-Z0-9._%+-]+@naver\.com$/,
    id: /^[a-zA-Z][a-zA-Z0-9]{3,19}$/,
    pw: /^[a-zA-Z0-9]{8,40}$/
};
const MSG =
{
    name: '닉네임 형식이 올바르지 않습니다.',
    mail: '이메일 형식이 올바르지 않습니다.',
    id: 'ID 형식이 올바르지 않습니다.',
    pw: 'PW 형식이 올바르지 않습니다.'
};

// 형식 검증
function regexCheck(inputId, regex, failMsg)
{
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (!regex.test(value))
    {
        showToast(failMsg);
        input.focus();
        return false;
    }

    return true;
}

// 원샷 검증
function validate(inputId, inputName, regex, failMsg)
{
    if (!check(inputId, inputName)) { return false; }
    if (regex && !regexCheck(inputId, regex, failMsg)) { return false; }
    return true;
}

// ID 중복 검사
function idUsingCheck()
{
    if (!validate("idInput", "ID", REGEX.id, MSG.id)) { return; }

    const id = getValue("idInput");
    const ok = document.getElementById("ok");
    const no = document.getElementById("no");

    ok.style.display = "none";
    no.style.display = "none";

    fetchJson(`/api/auth/exists/${encodeURIComponent(id)}`, { method: "GET" }, {
        defaultErrorMessage: "중복 확인 중 오류가 발생했습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        if (data.exists === true)
        {
            ok.style.display = "none";
            no.style.display = "inline";
            showToast("이미 사용 중인 ID입니다.");
        }
        else
        {
            no.style.display = "none";
            ok.style.display = "inline";
            showToast("사용 가능한 ID입니다.");
        }
    })
    .catch(() =>
    {
        ok.style.display = "none";
        no.style.display = "none";
        showToast("중복 확인 중 오류가 발생했습니다.");
    });
}

// ID 중복 검사 상태 초기화
function resetIdCheck()
{
    const ok = document.getElementById("ok");
    const no = document.getElementById("no");

    ok.style.display = "none";
    no.style.display = "none";
}

// ID 중복 검사 여부 확인
function isIdUsingCheck()
{
    const ok = document.getElementById("ok");
    if (ok.style.display !== "inline") { showToast('ID 중복 확인을 진행해주세요.'); return false; }
    return true;
}

// 정책 동의 여부 확인
function termsCheck()
{
    const terms = document.querySelector("#terms input[type='checkbox']");
    const privacy = document.querySelector("#privacy input[type='checkbox']");
    const age = document.querySelector("#age input[type='checkbox']");

    if (!terms.checked) { showToast('이용약관에 동의하세요.'); return false; }
    if (!privacy.checked) { showToast('개인정보 처리방침에 동의하세요.'); return false; }
    if (!age.checked) { showToast('만 14세 미만은 가입이 불가합니다.'); return false; }

    return true;
}

// 비밀번호 보기
function pwToggle()
{
    const pwInputs = document.getElementsByName("pwInput");
    pwInputs.forEach(input =>
    { if (input.type === "password") { input.type = "text"; } else { input.type = "password"; } });
}

// 회원가입 요청
function join()
{
    if (!validate("nameInput", "닉네임", REGEX.name, MSG.name)) { return; }
    if (!validate("idInput", "ID", REGEX.id, MSG.id)) { return; }
    if (!isIdUsingCheck()) { return; }
    if (!validate("pwInput", "비밀번호", REGEX.pw, MSG.pw)) { return; }
    if (!validate("mailInput", "메일 주소", REGEX.mail, MSG.mail)) { return; }
    if (!termsCheck()) { return; }

    const payload =
    {
        userId: getValue("idInput"),
        userName: getValue("nameInput"),
        userPw: getValue("pwInput"),
        userMail: getValue("mailInput")
    };

    postJson("/api/auth/join/request", payload, {
        defaultErrorMessage: "회원가입 중 오류가 발생했습니다.",
        toastOnSuccess: "회원가입 메일을 발송했습니다.",
        parseJson: false
    });
}

// 토큰 재발급
function token()
{
    if (!validate("findInput", "메일 주소", REGEX.mail, MSG.mail)) { return; }

    const payload = { userMail: getValue("findInput") };

    postJson("/api/auth/join/resend", payload, {
        defaultErrorMessage: "토큰 재발급 중 오류가 발생했습니다.",
        toastOnSuccess: "토큰을 재발급했습니다.",
        parseJson: false
    });
}

// 로그인
function login()
{
    if (!validate("idInput", "ID", REGEX.id, MSG.id)) { return; }
    if (!validate("pwInput", "비밀번호", REGEX.pw, MSG.pw)) { return; }

    const payload = { userId: getValue("idInput"), userPw: getValue("pwInput") };

    postJson("/api/auth/login", payload, {
        defaultErrorMessage: "로그인 중 오류가 발생했습니다.",
        parseJson: false
    }).then((r) => { if (!r) return; location.href = "/?login=1"; });
}

// 계정 찾기
function findAccount()
{
    if (!validate("mailInput", "메일 주소", REGEX.mail, MSG.mail)) { return; }

    const payload = { userMail: getValue("mailInput") };

    postJson("/api/auth/password/reset/request", payload, {
        defaultErrorMessage: "계정 찾기 중 오류가 발생했습니다.",
        toastOnSuccess: "계정 복구 메일을 발송했습니다.",
        parseJson: false
    });
}

// 비밀번호 변경
function changePw()
{
    if (!validate("pwInput", "기존 비밀번호", REGEX.pw, MSG.pw)) { return; }
    if (!validate("newPwInput", "변경할 비밀번호", REGEX.pw, MSG.pw)) { return; }

    const payload = { currentPw: getValue("pwInput"), newPw: getValue("newPwInput") };

    postJson("/api/auth/password/change", payload,
    { defaultErrorMessage: "비밀번호 변경 중 오류가 발생했습니다.", parseJson: false })
    .then((r) => { if (!r) return; location.href = "/?logout=1"; });
}

// 회원 탈퇴
function withdraw()
{
    if (!validate("pwInput", "비밀번호", REGEX.pw, MSG.pw)) { return; }

    const payload = { userPw: getValue("pwInput") };

    postJson("/api/auth/withdraw/request", payload, {
        defaultErrorMessage: "회원 탈퇴 요청 중 오류가 발생했습니다.",
        toastOnSuccess: "회원 탈퇴 확인 메일을 발송했습니다.",
        parseJson: false
    });
}