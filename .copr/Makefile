rpmbuild_src = $(shell rpmbuild --eval '%{_sourcedir}')
.PHONY: prepare srpm rpm

prepare:
	dnf -y install rpmdevtools rpm-build || true
	rpmdev-setuptree
	cp *.patch $(rpmbuild_src)
	cp macros.zig $(rpmbuild_src)
	spectool -g -R $(spec)

srpm: prepare
	rpmbuild -bs --define "_srcrpmdir $(outdir)" $(spec)

rpm: prepare
	rpmbuild -bb --define "_rpmdir $(outdir)" $(spec)
